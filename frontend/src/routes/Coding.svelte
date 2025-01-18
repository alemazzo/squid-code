<script>
  import { onMount } from "svelte";
  import { getLeetcodeInfo, getLeetCodeSubmission } from "../api/leetcode";
  import { navigate } from "svelte-routing";
	import Problem from "./Problem.svelte";

  export let id;

  let problem;
  let timeLeft = 0; // 20 minutes in seconds
  let timerInterval;

  // Format time into MM:SS
  const formatTime = (time) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
  };

  const secondsToMinutes = (time) => {
    const minutes = Math.floor(time / 60);
    return minutes;
  };

  async function checkSubmission() {
      let submission = await getLeetCodeSubmission(id, localStorage.getItem("username"));
      console.log("Submission: ", submission);
      if (submission != null) {
          if (submission.statusDisplay === "Accepted") {
              clearInterval(timerInterval);
              let problem = await getLeetcodeInfo(id);
              console.log("LeetCode Problem: ", problem);
              sendWinNotification(problem);
              navigate(`/problem/${problem["titleSlug"]}/win`);
          } else if (submission.statusDisplay === "Internal Error") {
              // Retry after 10 seconds
          } else {
              clearInterval(timerInterval);
              sendFailNotification();
              navigate(`/fail`);
          }
      }
  }

  // Decrement time every second
  const startTimer = () => {
    sendInitialNotification();
    timerInterval = setInterval(async () => {
      if (timeLeft > 0) {
        timeLeft--;
        if (timeLeft % 60 === 0) {
          sendNotification(timeLeft);
        }
        if (timeLeft % 5 === 0) {
          checkSubmission();
        }
      } else {
        clearInterval(timerInterval);
        sendFailNotification();
        navigate(`/fail`);
      }
    }, 1000);
    
  };

  // Function to send initial notification
  function sendInitialNotification() {
      new Notification("Coding interview started", {
          body: "You have " + secondsToMinutes(timeLeft) + " minutes to solve the problem",
      });
  }

  // Function to send notification
  function sendNotification(seconds) {
      new Notification(`${secondsToMinutes(seconds)} minutes remaining`);
  }

  // Function to send win notification
  function sendWinNotification(problem) {
      new Notification("Congratulations!", {
          body: `You have successfully solved the problem: ${problem.title}`,
      });
  }

  // Function to send fail notification
  function sendFailNotification() {
      new Notification("You have failed!", {
          body: "Please try again",
      });
  }

  // Start the timer immediately when the component is mounted
  onMount(async () => {
     problem = await getLeetcodeInfo(id);
     if (problem.difficulty === "Easy") {
         timeLeft = 15 * 60; // 10 minutes for Easy problems
      } else if (problem.difficulty === "Medium") {
          timeLeft = 30 * 60; // 15 minutes for Medium problems
      } else {
          timeLeft = 45 * 60; // 20 minutes for Hard problems
      }
      if (Notification.permission !== "granted") {
          Notification.requestPermission().then(permission => {
              if (permission === "granted") {
                  startTimer();
              } else {
                  console.log("Notification permission denied");
              }
          });
      } else {
          startTimer();
      }
  });
</script>

<style>
  /* General Styles */
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

  .container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 100%;
    box-sizing: border-box;
  }

  /* Timer Style */
  .timer {
    font-size: 8rem;
    font-weight: bold;
    color: #f39c12;
    background-color: #222;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 0 20px rgba(255, 0, 0, 0.7);
  }

  /* Message and Status */
  .status-message {
    font-size: 2rem;
    font-weight: bold;
    color: #f39c12;
    margin-top: 40px;
    margin-bottom: 40px;
  }

  .status-waiting {
    color: #f39c12;
  }

  .status-found {
    color: #28a745; /* Green for success */
  }

  .status-failed {
    color: #dc3545; /* Red for failure */
  }

  /* GIF Styling */
  .gif {
    margin-top: 30px;
    width: 100%; /* Adjust size as needed */
    height: auto;
    display: block;
    margin-left: auto;
    margin-right: auto;
  }
</style>

<div class="container">
  {#if problem}
  <div class="status-message">
    <p class="status-waiting"> Problem: {@html problem.title}</p>
  </div>
  {/if}
  
  <div class="timer">
      {formatTime(timeLeft)}
  </div>

  <div class="status-message">
    <p class="status-waiting">Waiting for your submission...</p>
    <!-- Squid Game Pink Man GIF (Centered) -->
    <img class="gif" src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExazBwMG92ZHJicnZiamp4M2w5OGVuM2twOXQxMXlucHlpbHpjc24zeCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/eIG0HfouRQJQr1wBzz/giphy.gif" alt="Squid Game Pink Man" />
  </div>
</div>
