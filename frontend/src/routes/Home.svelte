<script>
    import { onMount } from "svelte";
    import { getRandomLeetCodeQuestion } from "../api/leetcode";

    function startGame(level) {
        console.log(`Starting game with level: ${level}`);
        getRandomLeetCodeQuestion("medium").then((question) => {
            if (question) {
                console.log("Random LeetCode Question:");
                console.log(`Title: ${question.title}`);
                console.log(`Difficulty: ${question.difficulty}`);
                console.log(`Link: ${question.link}`);
            }
        });
    }

    onMount(() => {
        console.log("Mounted Home.svelte");
    });
</script>

<style>
    :global(body) {
        margin: 0;
        font-family: 'Arial', sans-serif;
        background: radial-gradient(circle, #1a1a1a 0%, #000000 70%);
        color: #ffffff;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        overflow: hidden;
    }

    .container {
        text-align: center;
    }

    h1 {
        font-size: 4rem;
        margin-bottom: 3rem;
        color: #ff006e;
        text-transform: uppercase;
        letter-spacing: 0.2rem;
        text-shadow: 0 0 8px #ff006e, 0 0 15px #ff006e;
    }

    .buttons {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 2rem;
    }

    .button {
        position: relative;
        background-color: #000000;
        border: 4px solid #ff006e;
        color: #ff006e;
        font-size: 1.2rem;
        font-weight: bold;
        text-align: center;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
        text-transform: uppercase;
        box-shadow: 0 0 10px #ff006e, 0 0 20px #ff006e;
        transition: all 0.3s ease;
    }

    .button:hover {
        background-color: #ff006e;
        color: #000000;
        box-shadow: 0 0 20px #ff006e, 0 0 40px #ff006e;
    }

    .button:active {
        transform: scale(0.95);
    }

    /* Circle (Easy) */
    .button.easy {
        border-radius: 50%;
        width: 150px;
        height: 150px;
    }

    /* Triangle (Medium) */
    .button.medium {
        width: 170px;
        height: 150px;
        clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
        font-size: 1rem;
        position: relative;
    }

    .button.medium span {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 1; /* Ensure text is on top */
    }

    .button.medium::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        clip-path: inherit; /* Matches the triangle shape */
        background: inherit; /* Matches the button's background */
        z-index: -1;
        filter: blur(4px); /* Creates the glowing effect */
        box-shadow: 0 0 10px #ff006e, 0 0 20px #ff006e, 0 0 30px #ff006e;
    }

    /* Square (Hard) */
    .button.hard {
        border-radius: 0;
        width: 150px;
        height: 150px;
    }

    /* Background */
    .background {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: -1;
        background-image: url('https://images.unsplash.com/photo-1635735823246-88b905b6e094?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080');
        background-size: cover;
        background-position: center;
        filter: brightness(0.4) blur(3px);
    }
</style>

<div class="background"></div>

<div class="container">
    <h1>Squid Code</h1>
    <div class="buttons">
        <button class="button easy" on:click={() => startGame('easy')}>Easy</button>
        <button class="button medium" on:click={() => startGame('medium')}>
            <span>Medium</span>
        </button>
        <button class="button hard" on:click={() => startGame('hard')}>Hard</button>
    </div>
</div>
