/**
 * Tiny history-API router for Svelte 5.
 *
 * `svelte-routing` is stuck on Svelte 4 (its last release 2.13.0 crashes at
 * mount on Svelte 5 with "Cannot read properties of undefined (reading
 * 'before')"), so the application ships its own router instead of depending on
 * an unmaintained package.
 *
 * The public API mirrors the parts of `svelte-routing` the app used:
 * `navigate()`, `<Router>`, `<Route path component />` and `<Link>`.
 *
 * Routes are matched against `window.location.pathname`; a segment starting
 * with `:` is a parameter and is passed to the routed component as a prop
 * (e.g. `/problem/:id` renders `<Problem id="two-sum" />`).
 */
import { writable, type Writable } from "svelte/store";

function currentPath(): string {
  return typeof window === "undefined" ? "/" : window.location.pathname;
}

/** The current pathname. Components subscribe to it with `$pathname`. */
export const pathname: Writable<string> = writable(currentPath());

function sync(): void {
  pathname.set(currentPath());
}

if (typeof window !== "undefined") {
  // Back/forward buttons and any other history change.
  window.addEventListener("popstate", sync);
}

/**
 * Navigate to `to`, pushing (or replacing) a history entry, and notify the
 * subscribed components.
 */
export function navigate(
  to: string,
  { replace = false }: { replace?: boolean } = {},
): void {
  if (replace) {
    window.history.replaceState({}, "", to);
  } else {
    window.history.pushState({}, "", to);
  }
  sync();
}

/**
 * Match `path` (the current pathname) against a `pattern` such as
 * `/problem/:id/coding`.
 *
 * Returns the extracted parameters, or `null` when the pattern does not match.
 * Matching is exact: `/problem/:id` does not match `/problem/foo/coding`.
 */
export function matchPath(
  pattern: string,
  path: string,
): Record<string, string> | null {
  const patternSegments = pattern.split("/").filter(Boolean);
  const pathSegments = path.split("/").filter(Boolean);

  if (patternSegments.length !== pathSegments.length) {
    return null;
  }

  const params: Record<string, string> = {};

  for (let i = 0; i < patternSegments.length; i++) {
    const patternSegment = patternSegments[i];
    const pathSegment = pathSegments[i];

    if (patternSegment.startsWith(":")) {
      params[patternSegment.slice(1)] = decodeURIComponent(pathSegment);
    } else if (patternSegment !== pathSegment) {
      return null;
    }
  }

  return params;
}
