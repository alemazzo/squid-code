<script>
    import { onMount } from "svelte";
	import { getLeetcodeInfo, getRandomLeetCodeQuestion } from "../api/leetcode";
    import { navigate } from "svelte-routing";
    export let id; 
    let problem;

    function startGame(level) {
        console.log(`Starting game with level: ${level}`);
        getRandomLeetCodeQuestion(level).then(async (question) => {
            console.log("Random LeetCode Question:");
            navigate(`/squid-code/problem/${question["titleSlug"]}`);
        });
    }

    function goHome() {
        window.location.href = "/";
    }

    async function nextRound() {
        let difficulty = problem["difficulty"];
        let newProblem = await getRandomLeetCodeQuestion(difficulty);
        navigate(`/squid-code/problem/${newProblem["titleSlug"]}`);
    }

    onMount(async () => {
        try {
            problem = await getLeetcodeInfo(id);
            console.log("LeetCode Problem: ", problem);
        } catch (error) {
        console.error("Error fetching problem: ", error);
        }
    });

  </script>
  
  <style>

    body {
      margin: 0;
      font-family: 'Arial', sans-serif;
      background-color: #1e1e1e;
      color: white;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      text-align: center;
    }
  
    .win-container {
      background: #222;
      border-radius: 20px;
      box-shadow: 0 0 15px rgba(0, 255, 0, 0.8);
      padding: 40px;
      max-width: 600px;
      width: 90%;
      text-align: center;
    }
  
    h1 {
      font-size: 3rem;
      color: #00ff00;
      margin-bottom: 20px;
    }
  
    p {
      font-size: 1.2rem;
      line-height: 1.6;
      color: #ccc;
      margin-bottom: 20px;
    }
  
    .btn {
      background-color: #00ff00;
      color: #1e1e1e;
      border: none;
      padding: 10px 20px;
      font-size: 1.2rem;
      border-radius: 10px;
      cursor: pointer;
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
  
    .btn:hover {
      transform: scale(1.1);
      box-shadow: 0 0 15px rgba(0, 255, 0, 0.8);
    }
  
  </style>
  
  <div class="win-container">
    <h1>You Win!</h1>
    <p>
      Congratulations! You’ve successfully solved the problem and proved your worth. 
      But let’s not get carried away. We'll let you know if we need you.
    </p>
    <p>
      Keep an eye on your inbox. Or don’t. We’ll call you… maybe.
    </p>
    <button class="btn" on:click="{nextRound}">
       Next Round 🔄
    </button>
    <button class="btn" on:click="{goHome}">
        Go Home, I'm a Quitter
     </button>
  </div>
  