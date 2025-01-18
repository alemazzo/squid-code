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

  const secondsToMinutesAndSeconds = (time) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes} minutes and ${seconds} seconds`;
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
        timeLeft--;
        if (timeLeft % 10 === 0) {
          sendNotification(timeLeft);
        }
        if (timeLeft % 10 === 0) {
          checkSubmission(timerInterval);
        }
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
      new Notification(`Timer Update: ${secondsToMinutesAndSeconds(seconds)} remaining`, {
          body: "Keep going! You're almost there!",
          icon: "https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon.png",
      });
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

/* Spinning Wheel */
.spinner {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 8px solid #f3f3f3;
    border-top: 8px solid #f39c12;
    border-radius: 50%;
    width: 60px;
    height: 60px;
    animation: spin 2s linear infinite;
    
    /* Center the spinner inside the container */
    display: block;
    margin-left: auto;
    margin-right: auto;
}

/* Add the spin animation */
@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

</style>

<div class="container">
  <div class="timer">
      {formatTime(timeLeft)}
  </div>

  <div class="status-message">
      {#if submissionStatus === 'waiting'}
          <p class="status-waiting">Waiting for your submission...</p>
          <div class="spinner"></div>
      {:else if submissionStatus === 'found'}
          <p class="status-found">{submissionResult}</p>
      {:else if submissionStatus === 'failed'}
          <p class="status-failed">{submissionResult}</p>
      {/if}
  </div>
</div>
