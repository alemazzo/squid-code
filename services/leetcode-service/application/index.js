import express, { json } from "express";
import cors from "cors"; // Import cors middleware
import { LeetCode } from 'leetcode-query';
import client from "prom-client";

const leetcode = new LeetCode();

const app = express();
const PORT = 3000;

// Prometheus instrumentation: default process/runtime metrics (nodejs_*, process_*)
// plus an HTTP counter and latency histogram per method/route/status.
client.collectDefaultMetrics();

const httpRequestsTotal = new client.Counter({
    name: "http_requests_total",
    help: "Number of HTTP requests handled, by method, route and status code",
    labelNames: ["method", "route", "status"],
});

const httpRequestDurationSeconds = new client.Histogram({
    name: "http_request_duration_seconds",
    help: "HTTP request latency in seconds, by method, route and status code",
    labelNames: ["method", "route", "status"],
    buckets: [0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10],
});

function routeLabel(req) {
    // Express 5 sets req.route only when the request actually matched a route.
    if (req.route && req.route.path) {
        return `${req.baseUrl || ""}${req.route.path}`;
    }
    return "unmatched";
}

// Enable CORS for all routes
app.use(cors());

// Parse JSON request bodies
app.use(json());

// Prometheus scrape endpoint (not instrumented itself, to keep the counter clean).
app.get("/metrics", async (req, res) => {
    res.set("Content-Type", client.register.contentType);
    res.end(await client.register.metrics());
});

app.use((req, res, next) => {
    if (req.path === "/metrics") {
        return next();
    }
    const end = httpRequestDurationSeconds.startTimer();
    res.on("finish", () => {
        const labels = {
            method: req.method,
            route: routeLabel(req),
            status: String(res.statusCode),
        };
        httpRequestsTotal.inc(labels);
        end(labels);
    });
    next();
});

async function getRandomProblem(level) {
    let problem = null;
    do {
        const randomNumber = Math.floor(Math.random() * 100);
        console.log(`Fetching random ${level} problem #${randomNumber}`);
        const problems = await leetcode.problems({
            offset: randomNumber,
            limit: 1,
            filters: {
                difficulty: level.toUpperCase(),
            }
        })
        console.log(problems);
        let question = problems["questions"][0];
        problem = await leetcode.problem(question["titleSlug"]);
    } while (problem["isPaidOnly"]);
    return problem;
}

async function getProblem(id) {
    console.log(`Fetching problem #${id}`);
    const problem = await leetcode.problem(id);
    return problem;
}

async function getLatestSubmissionOfProblem(problemId, user) {
    console.log(`Fetching latest submission of problem ${problemId} by ${user}`);
    const submission = (await leetcode.recent_submissions(user, 1))[0];
    if (submission["titleSlug"] == problemId) {
        return submission;
    }
    return null;
}

// Proxy route for LeetCode API
app.get("/leetcode/random-problem/:level", async (req, res) => {
    const { level } = req.params;
    console.log(`Fetching random ${level} problem`);
    const problem = await getRandomProblem(level);
    res.json(problem);
})


app.get("/leetcode/problems/:id", async (req, res) => {
    const { id } = req.params;
    console.log(`Fetching problem #${id}`);
    const problem = await getProblem(id);
    res.json(problem);
})


app.get("/leetcode/submissions/:problemId/:user", async (req, res) => {
    const { problemId, user } = req.params;
    console.log(`Fetching submissions for problem ${problemId} by ${user}`);
    const submission = await getLatestSubmissionOfProblem(problemId, user);
    res.json(submission);
})


// Start the proxy server
app.listen(PORT, () => {
    console.log(`Proxy server running on http://localhost:${PORT}`);
});
