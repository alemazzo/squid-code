<script>
  import { onMount } from "svelte";
  import { getRandomLeetCodeQuestion } from "../api/leetcode";
  import { navigate } from "svelte-routing";
  
  let username = "";
  let isUsernameValid = false;
  
  function validateUsername() {
    isUsernameValid = username.length > 4;
    if (isUsernameValid) {
      localStorage.setItem("username", username);
    }
  }
  
  async function startGame(level) {
    console.log(`Starting game with level: ${level}`);
    let problem = await getRandomLeetCodeQuestion(level);
    console.log("Random LeetCode Question: ", problem);
    navigate(`/squid-code/problem/${problem["titleSlug"]}`);
  }
  
  onMount(() => {
    console.log("Mounted Home.svelte");
    const savedUsername = localStorage.getItem("username");
    if (savedUsername) {
      username = savedUsername;
      validateUsername();
    }
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
    position: relative;
    z-index: 1;
  }

  h1 {
    font-size: 6rem;
    margin-bottom: 3rem;
    color: #ff006e;
    text-transform: uppercase;
    letter-spacing: 0.2rem;
    text-shadow: 0 0 8px #ff006e, 0 0 15px #ff006e;
  }

  p.description {
  font-size: 1.1rem;
  color: #ffffff;
  margin-bottom: 2rem;
  line-height: 1.8;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  background: rgba(0, 0, 0, 0.6); /* Semi-transparent background */
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  font-family: 'Roboto', sans-serif;
  text-align: center;
}

p.description br {
  margin-bottom: 1rem;
}

p.description span {
  color: #ff006e; /* Highlighted text color */
  font-weight: bold;
  font-style: italic;
}


  .input-container {
    margin-bottom: 2rem;
  }

  input {
    font-size: 1.2rem;
    padding: 10px;
    border-radius: 5px;
    border: 2px solid #ff006e;
    outline: none;
    width: 300px;
    color: #ffffff;
    background-color: #1a1a1a;
    text-align: center;
    transition: box-shadow 0.3s ease;
  }

  input:focus {
    box-shadow: 0 0 10px #ff006e, 0 0 20px #ff006e;
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

  .button:disabled {
    border: 4px solid #555;
    color: #555;
    background-color: #333;
    box-shadow: none;
    cursor: not-allowed;
  }

  .button:hover:enabled {
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
  
  <!-- Added Description Section -->
  <p class="description">
    Welcome to Squid Code! 
    <br /> 
    Pick a difficulty and get ready for a challenge. 
    <br /> 
    Your LeetCode username will be needed to play
    <br />
    Things are about to get interesting...
  </p>
  
  

  <div class="input-container">
    <input
      type="text"
      placeholder="Enter LeetCode Username"
      bind:value="{username}"
      on:input="{validateUsername}"
      autocomplete="off"
    />
  </div>

  <div class="buttons">
    <button class="button easy" disabled={!isUsernameValid} on:click={() => startGame('easy')}>Easy</button>
    <button class="button medium" disabled={!isUsernameValid} on:click={() => startGame('medium')}>
      <span>Medium</span>
    </button>
    <button class="button hard" disabled={!isUsernameValid} on:click={() => startGame('hard')}>Hard</button>
  </div>
</div>
