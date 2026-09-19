#!/usr/bin/env node
/**
 * Smoke test for the built single page application.
 *
 * It serves `dist/` with `vite preview`, loads it in a headless Chromium and
 * asserts that the app actually mounted and that the router renders a route.
 * The check exists because the app once shipped a bundle that mounted nothing
 * (`<main>` was left empty) and no CI step caught it.
 *
 * Exit code 0 = the app renders, 1 = it does not (or a page error was logged).
 */
import { spawn } from "node:child_process";
import { setTimeout as sleep } from "node:timers/promises";
import process from "node:process";
import { fileURLToPath } from "node:url";
import path from "node:path";
import { chromium } from "playwright";

const PORT = Number(process.env.PREVIEW_PORT ?? 4173);
const BASE_URL = `http://127.0.0.1:${PORT}`;

const results = [];

function check(name, ok, detail = "") {
  results.push({ name, ok, detail });
  // The detail only matters when the check fails; keep the happy path quiet.
  console.log(`${ok ? "ok  " : "FAIL"} ${name}${!ok && detail ? ` — ${detail}` : ""}`);
}

async function waitForServer(timeoutMs = 30_000) {
  const deadline = Date.now() + timeoutMs;
  while (Date.now() < deadline) {
    try {
      const response = await fetch(`${BASE_URL}/`);
      if (response.ok) return true;
    } catch {
      // server not up yet
    }
    await sleep(500);
  }
  return false;
}

// Run the local vite binary with the current node: a single process that can
// be stopped with SIGTERM (an `npx` wrapper would leave `vite preview` behind).
const viteBin = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  "../node_modules/vite/bin/vite.js",
);
const server = spawn(
  process.execPath,
  [viteBin, "preview", "--host", "127.0.0.1", "--port", String(PORT), "--strictPort"],
  { stdio: ["ignore", "pipe", "pipe"] },
);
// Keep the preview output: it is the only clue when the server does not
// come up. It is dumped in the failure branch below.
const previewOutput = [];
server.stdout.on("data", (chunk) => previewOutput.push(String(chunk)));
server.stderr.on("data", (chunk) => previewOutput.push(String(chunk)));

let browser;
let exitCode = 0;

try {
  if (!(await waitForServer())) {
    throw new Error(
      `${BASE_URL} non risponde: "vite preview" non è partito${previewOutput.length ? `\n--- output di vite preview ---\n${previewOutput.join("")}` : " (nessun output dal processo)"}`,
    );
  }

  browser = await chromium.launch();
  const page = await browser.newPage();

  const pageErrors = [];
  page.on("pageerror", (error) => pageErrors.push(String(error)));

  // 1. The home route renders something inside #app.
  await page.goto(`${BASE_URL}/`, { waitUntil: "load" });
  await page.waitForLoadState("networkidle").catch(() => {});
  const homeText = (await page.locator("#app").innerText()).trim();
  check("home: #app contains rendered content", homeText.length > 0, "paint empty: nothing rendered inside #app");
  check(
    "home: the Home route is rendered",
    homeText.includes("Squid Code"),
    "testo della route / non trovato",
  );

  // 2. A deep link renders the component bound to that route.
  await page.goto(`${BASE_URL}/problem/two-sum`, { waitUntil: "load" });
  await page.waitForLoadState("networkidle").catch(() => {});
  const hasProblemView = (await page.locator("main .start-button").count()) > 0;
  check("deep link /problem/:id renders the Problem route", hasProblemView);

  // 3. Navigating in the browser (history/popstate) swaps the rendered route.
  await page.goto(`${BASE_URL}/`, { waitUntil: "load" });
  await page.evaluate(() => {
    window.history.pushState({}, "", "/fail");
    window.dispatchEvent(new PopStateEvent("popstate"));
  });
  const failText = (await page.locator("#app").innerText()).trim();
  check(
    "client side navigation renders the /fail route",
    failText.includes("YOU FAILED"),
    failText.slice(0, 60).replace(/\s+/g, " "),
  );

  // 4. No uncaught exception while mounting/navigating.
  check(
    "no uncaught error in the page",
    pageErrors.length === 0,
    pageErrors.join(" | "),
  );
} catch (error) {
  check("smoke test eseguito", false, String(error));
} finally {
  if (browser) await browser.close().catch(() => {});
  server.kill("SIGTERM");
}

const failed = results.filter((result) => !result.ok);

if (failed.length > 0) {
  console.error(`\n${failed.length} verifiche fallite su ${results.length}.`);
  exitCode = 1;
} else {
  console.log(`\n${results.length} verifiche superate.`);
}

process.exit(exitCode);
