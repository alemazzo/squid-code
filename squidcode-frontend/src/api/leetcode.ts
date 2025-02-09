const baseUrl = 'https://leetcode.com';
const endpoint = "https://leetcode.squidcode.xyz/leetcode/";


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
