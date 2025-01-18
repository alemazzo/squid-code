import express, { json } from "express";
import fetch from "node-fetch";
import cors from "cors"; // Import cors middleware
import { LeetCode } from 'leetcode-query';

const leetcode = new LeetCode();

const app = express();
const PORT = 3000;

// Enable CORS for all routes
app.use(cors());

// Parse JSON request bodies
app.use(json());

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
    if (submission) {
        res.json(submission);
    }
    else {
        res.status(404).send("Submission not found");
    }
})


// Start the proxy server
app.listen(PORT, () => {
    console.log(`Proxy server running on http://localhost:${PORT}`);
});
