<script>
  import { onMount } from "svelte";
  import { getLeetcodeInfo, getLeetCodeSubmission } from "../api/leetcode";
  import { navigate } from "svelte-routing";

  export let id;

  let timeLeft = 20 * 60; // 20 minutes in seconds
  let timerInterval;
  let submissionStatus = "waiting"; // Possible values: "waiting", "found", "failed"
  let submissionResult = "";

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

  async function checkSubmission(timer) {
      let submission = await getLeetCodeSubmission(id, localStorage.getItem("username"));
      console.log("Submission: ", submission);
      if (submission != null) {
          clearInterval(timerInterval);
          if (submission.statusDisplay === "Accepted") {
              submissionStatus = "found";
              submissionResult = "Congratulations! Your solution is Accepted!";
              let problem = await getLeetcodeInfo(id);
              console.log("LeetCode Problem: ", problem);
              navigate(`/squid-code/problem/${problem["titleSlug"]}/win`);
          } else {
              submissionStatus = "failed";
              submissionResult = `Submission Failed: ${submission.statusDisplay}`;
              navigate(`/squid-code/fail`);
          }
      }
  }

  // Decrement time every second
  const startTimer = () => {
    timerInterval = setInterval(async () => {
      if (timeLeft > 0) {
        if (timeLeft % 60 === 0) {
          sendNotification(timeLeft);
        }
        if (timeLeft % 10 === 0) {
          checkSubmission(timerInterval);
        }
        timeLeft--;
      } else {
        clearInterval(timerInterval);
        submissionStatus = "failed";
        submissionResult = "Time's up! You failed the coding interview.";
        alert(submissionResult);
        navigate(`/squid-code/fail`);
      }
    }, 1000);
  };

  // Function to send notification
  function sendNotification(seconds) {
      new Notification(`${secondsToMinutes(seconds)} remaining`);
  }

  // Start the timer immediately when the component is mounted
  onMount(() => {
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
  <div class="timer">
      {formatTime(timeLeft)}
  </div>

  <div class="status-message">
      {#if submissionStatus === 'waiting'}
          <p class="status-waiting">Waiting for your submission...</p>
          <!-- Squid Game Pink Man GIF (Centered) -->
          <img class="gif" src="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExazBwMG92ZHJicnZiamp4M2w5OGVuM2twOXQxMXlucHlpbHpjc24zeCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/eIG0HfouRQJQr1wBzz/giphy.gif" alt="Squid Game Pink Man" />
      {:else if submissionStatus === 'found'}
          <p class="status-found">{submissionResult}</p>
          <!-- You can add a congratulatory GIF here if needed -->
      {:else if submissionStatus === 'failed'}
          <p class="status-failed">{submissionResult}</p>
          <!-- You can add a failure GIF here if needed -->
      {/if}
  </div>
</div>
