<script>
    import { onMount } from "svelte";
    import { getRandomLeetCodeQuestion, getLeetcodeInfo } from "../api/leetcode";
    
    export let id;
  let problem = {};
  
  // Initialize timer state
  let timeLeft = 10 * 60; // 10 minutes in seconds
  let interval;

  // Function to format the time in mm:ss format
  const formatTime = (timeInSeconds) => {
    const minutes = Math.floor(timeInSeconds / 60);
    const seconds = timeInSeconds % 60;
    return `${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  };

  // Countdown function that decrements the timer each second
  const startTimer = () => {
    interval = setInterval(() => {
      if (timeLeft > 0) {
        timeLeft -= 1;
      } else {
        clearInterval(interval); // Stop the timer when it reaches 0
        window.open("https://leetcode.com/problems/" + id, "_blank");
        window.location.href = "/coding"; // Redirect to the coding page
      }
    }, 1000);
  };

  // Fetch the problem info when the component mounts
  onMount(async () => {
    try {
      problem = await getLeetcodeInfo(id);
      console.log("LeetCode Problem: ", problem);
      startTimer(); // Start the timer once the problem is fetched
    } catch (error) {
      console.error("Error fetching problem: ", error);
    }
  });

  // Function to handle button click (optional)
  const handleStartCoding = () => {
    console.log("Starting to code...");
    // Open a new tab with the LeetCode problem link
    console.log("Problem: ", problem);
    window.open("https://leetcode.com/problems/" + id, "_blank");
    window.location.href = "/problem/" + id + "/coding"; // Redirect to the coding page
  };

  </script>
<style>

    /* Custom Scrollbar for Webkit Browsers (Chrome, Safari, etc.) */
::-webkit-scrollbar {
  width: 8px; /* Set the width of the scrollbar */
  height: 8px; /* For horizontal scrollbar */
}

::-webkit-scrollbar-track {
  background: #333; /* Track (background) color */
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background-color: #f39c12; /* Thumb (scrollbar) color */
  border-radius: 10px;
  border: 3px solid #333; /* Border around the scrollbar */
}

::-webkit-scrollbar-thumb:hover {
  background-color: #ff6347; /* Change color on hover */
}

/* Custom Scrollbar for Firefox */
html {
  scrollbar-width: thin; /* Thin scrollbar */
  scrollbar-color: #f39c12 #333; /* Thumb and Track color */
} 

    body {
      margin: 0;
      font-family: 'Arial', sans-serif;
      background-color: #1e1e1e;
      color: white;
      overflow-x: hidden; /* Prevent horizontal overflow */
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }
  
    .container {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      box-sizing: border-box;
      overflow: hidden; /* Ensure no overflow horizontally */
    }
  
    .problem-section {
  background: #222;
  padding: 40px 60px;
  width: 100%;
  margin-right: 250px;
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.7);
  overflow-y: auto;
  height: auto;
  max-height: 90vh;
  word-wrap: break-word;
  overflow-wrap: break-word;
  word-break: break-word;
  white-space: normal;
}
  
    h1 {
      font-size: 2.5rem;
      margin-bottom: 20px;
      font-weight: 600;
      word-wrap: break-word; /* Break long words */
      overflow-wrap: break-word; /* Prevents horizontal overflow */
    }
  
    p {
      font-size: 1.2rem;
      margin-bottom: 20px;
      line-height: 1.6;
      word-wrap: break-word; /* Wrap text if it overflows */
      overflow-wrap: break-word; /* Prevent horizontal overflow */
      word-break: break-word; /* Break long words */
    }
  
    .description, .content {
      margin-top: 20px;
      font-size: 1.2rem;
      line-height: 1.6;
      word-wrap: break-word; /* Wrap text within the container */
      overflow-wrap: break-word; /* Prevent horizontal overflow */
      word-break: break-word; /* Prevent words from overflowing */
      white-space: normal; /* Allow white space between paragraphs */
    }
  
    .description {
      max-height: 70vh;
      overflow-y: auto; /* Scrollable if content overflows */
    }
  
    .content {
      margin-top: 30px;
    }
  
    img {
      max-width: 100%; /* Prevent images from overflowing */
      height: auto;
    }

      /* Timer and Message */
      .timer-container {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #333;
  padding: 15px 25px;
  border-radius: 10px;
  box-shadow: 0 0 15px rgba(255, 0, 0, 0.7);
  width: 200px;
  font-size: 3rem; /* Increase the font size here */
  color: #f39c12;
  font-weight: bold; /* Optional: make the timer text bold */
    text-align: center;
    align-items: center;
}

  /* Start Coding Button */
  .start-button {
    position: fixed;
    top: 130px; /* Positioned below the timer */
    right: 20px;
    background-color: #f39c12;
    color: #1e1e1e;
    padding: 20px 40px;
    font-size: 2rem;
    font-weight: bold;
    border: none;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
    cursor: pointer;
    transition: background-color 0.3s ease;
    width: 200px; /* Make button the same width as the timer */
    text-align: center;
  }

  .start-button:hover {
    background-color: #ff6347;
  }

  </style>
  
  <div class="container">
    <div class="problem-section">
      <h1>{problem.title}</h1>
      <div class="description">{@html problem.description}</div>
      <div class="content">{@html problem.content}</div>
    </div>

    <div class="timer-container">
        {formatTime(timeLeft)}
    </div>
    <!-- Start Coding Button -->
    <button class="start-button" on:click={handleStartCoding}>
    Code
  </button>
  </div>