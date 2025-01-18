const baseUrl = 'https://leetcode.com';
const endpoint = "http://localhost:3000/leetcode/";

export async function getRandomLeetCodeQuestion(difficulty) {
    const url = "https://leetcode.com/graphql";
    const query = `
        query getProblems {
            problemsetQuestionList(
                categorySlug: ""
                filters: {}
                limit: 1000
                skip: 0
            ) {
                total
                questions {
                    questionId
                    title
                    titleSlug
                    difficulty
                    isPaidOnly
                }
            }
        }
    `;

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                query,
            }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.errors) {
            throw new Error(`GraphQL error: ${data.errors.map((e) => e.message).join(", ")}`);
        }

        const questions = data.data.problemsetQuestionList.questions;

        // Filter problems by difficulty and exclude paid-only problems
        const filteredQuestions = questions.filter(
            (q) => q.difficulty.toLowerCase() === difficulty.toLowerCase() && !q.isPaidOnly
        );

        if (filteredQuestions.length === 0) {
            throw new Error(`No problems found for difficulty level: ${difficulty}`);
        }

        // Select a random problem
        const randomIndex = Math.floor(Math.random() * filteredQuestions.length);
        const randomProblem = filteredQuestions[randomIndex];

        return randomProblem;
    } catch (error) {
        console.error("Error fetching random problem:", error);
        throw error;
    }
}

export async function getLeetcodeInfo(titleSlug) {
    const url = "https://leetcode.com/graphql";
    const query = `
        query getProblemDetails($titleSlug: String!) {
            question(titleSlug: $titleSlug) {
                questionId
                title
                titleSlug
                content
                difficulty
                likes
                dislikes
                isPaidOnly
                topicTags {
                    name
                    slug
                }
                codeSnippets {
                    lang
                    langSlug
                    code
                }
                exampleTestcases
            }
        }
    `;

    const variables = {
        titleSlug: titleSlug,
    };

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                query,
                variables,
            }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.errors) {
            throw new Error(`GraphQL error: ${data.errors.map((e) => e.message).join(", ")}`);
        }

        return data.data.question;
    } catch (error) {
        console.error("Error fetching problem from LeetCode:", error);
        throw error;
    }
}

export async function getLeetCodeSubmission(userId, problemId) {
    const url = "https://leetcode.com/graphql";
    const query = `
        query getUserSubmissions($userSlug: String!, $limit: Int!) {
            recentSubmissionList(userSlug: $userSlug, limit: $limit) {
                id
                title
                titleSlug
                statusDisplay
                lang
                timestamp
            }
        }
    `;

    const variables = {
        userSlug: userId, // LeetCode username
        limit: 1,         // Fetch only the latest submission
    };

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                query,
                variables,
            }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        if (data.errors) {
            throw new Error(`GraphQL error: ${data.errors.map((e) => e.message).join(", ")}`);
        }

        const submissions = data.data.recentSubmissionList;

        if (!submissions || submissions.length === 0) {
            return null; // No submissions found
        }

        const lastSubmission = submissions[0];

        if (lastSubmission.titleSlug === problemId) {
            return lastSubmission; // Return the submission if it matches the problemId
        }

        return null; // Otherwise, return null
    } catch (error) {
        console.error("Error fetching submissions from LeetCode:", error);
        throw error;
    }
}



/**

export async function getLeetcodeInfo(id: String) {
    try {
        const response = await fetch(endpoint + "problems/" + id);
        return await response.json();
    } catch (error: any) {
        console.error(error.message);
        return null;
    }
}
    
export async function getRandomLeetCodeQuestion(level: String) {
    try {
        const allowedLevels = ["easy", "medium", "hard"];
        if (!allowedLevels.includes(level.toLowerCase())) {
            throw new Error("Invalid level! Please choose from 'easy', 'medium', or 'hard'.");
        }
        const response = await fetch(endpoint + "random-problem/" + level);
        return await response.json();
    } catch (error: any) {
        console.error(error.message);
        return null;
    }
}

export async function getLeetCodeSubmission(id: String, username: String) {
    try {
        const response = await fetch(endpoint + "submissions/" + id + "/" + username);
        return await response.json();
    } catch (error: any) {
        console.error(error.message);
        return null;
    }
}   
 */