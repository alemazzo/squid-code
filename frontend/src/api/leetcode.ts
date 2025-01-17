const baseUrl = 'https://leetcode.com';
const endpoint = "https://leetcode.com/graphql";

export async function getRandomLeetCodeQuestion(level: String) {
    try {
        // Validate the level input
        const allowedLevels = ["easy", "medium", "hard"];
        if (!allowedLevels.includes(level.toLowerCase())) {
            throw new Error("Invalid level! Please choose from 'easy', 'medium', or 'hard'.");
        }

        // GraphQL query to fetch problems of a specific difficulty
        const query = `
            query getProblems($difficulty: String!) {
                problemsetQuestionList(
                    filters: { difficulty: $difficulty }
                    limit: 100
                ) {
                    questions: data {
                        titleSlug
                        title
                        difficulty
                    }
                }
            }
        `;

        // Send a POST request to the LeetCode API
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                query: query,
                variables: { difficulty: level.toUpperCase() },
            }),
        });

        // Parse the JSON response
        const result = await response.json();
        if (!result || !result.data || !result.data.problemsetQuestionList.questions) {
            throw new Error("Failed to fetch questions from LeetCode!");
        }

        // Extract the list of questions
        const questions = result.data.problemsetQuestionList.questions;

        if (questions.length === 0) {
            throw new Error(`No questions found for the difficulty level: ${level}`);
        }

        // Pick a random question from the list
        const randomIndex = Math.floor(Math.random() * questions.length);
        const randomQuestion = questions[randomIndex];

        // Return the random question
        return {
            title: randomQuestion.title,
            slug: randomQuestion.titleSlug,
            difficulty: randomQuestion.difficulty,
            link: `${baseUrl}/problems/${randomQuestion.titleSlug}`,
        };
    } catch (error: any) {
        console.error(error.message);
        return null;
    }
}

    