<script>
    import { onMount } from "svelte";
	import { getLeetcodeInfo, getLeetCodeSubmission } from "../api/leetcode";
    
    export let id;

    let timeLeft = 20 * 60; // 20 minutes in seconds
    let timerInterval;
  
    // Format time into MM:SS
    const formatTime = (time) => {
      const minutes = Math.floor(time / 60);
      const seconds = time % 60;
      return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
    };
  
    // Decrement time every second
    const startTimer = () => {
      timerInterval = setInterval(async () => {
        if (timeLeft > 0) {
          timeLeft--;
          // Every 5 seconds, show an alert message with the remaining time
            if (timeLeft % 5 === 0) {
                // Send a browser notification
                sendNotification(timeLeft);
                let submission = await getLeetCodeSubmission(id, localStorage.getItem("username"));
                console.log("Submission: ", submission);
                if (submission != null) {
                    if (submission.status === "Accepted") {
                        clearInterval(timerInterval);
                        alert("Congratulations! You passed the coding interview.");
                        let problem = await getLeetcodeInfo(id);
                        console.log("LeetCode Problem: ", problem);
                        window.location.href = "/squid-code/problem/" + problem["titleSlug"] + "/win"; // Redirect to the win page
                    }

                    if (submission.status === "Wrong Answer") {
                        clearInterval(timerInterval);
                        alert("Wrong Answer! You failed the coding interview.");
                        window.location.href = "/squid-code/fail"; // Redirect to the fail page
                    }

                    if (submission.status === "Time Limit Exceeded") {
                        clearInterval(timerInterval);
                        alert("Time Limit Exceeded! You failed the coding interview.");
                        window.location.href = "/squid-code/fail"; // Redirect to the fail page
                    }

                    if (submission.status === "Runtime Error") {
                        clearInterval(timerInterval);
                        alert("Runtime Error! You failed the coding interview.");
                        window.location.href = "/squid-code/fail"; // Redirect to the fail page
                    }

                    if (submission.status === "Memory Limit Exceeded") {
                        clearInterval(timerInterval);
                        alert("Memory Limit Exceeded! You failed the coding interview.");
                        window.location.href = "/squid-code/fail"; // Redirect to the fail page
                    }

                    if (submission.status === "Compile Error") {
                        clearInterval(timerInterval);
                        alert("Compile Error! You failed the coding interview.");
                        window.location.href = "/fail"; // Redirect to the fail page
                    }

                    if (submission.status === "Unknown Error") {
                        clearInterval(timerInterval);
                        alert("Unknown Error! You failed the coding interview.");
                        window.location.href = "/squid-code/fail"; // Redirect to the fail page
                    }

                }
            }
        } else {
          clearInterval(timerInterval);
          alert("Time's up! You failed the coding interview.");
            window.location.href = "/squid-code/fail";
        }
      }, 1000);
    };

    // Function to send notification
function sendNotification(seconds) {
    new Notification(`Timer Update: ${seconds} seconds remaining`, {
        body: "Keep going! You're almost there!",
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
  </style>
  
  <div class="container">
    <div>
      <div class="timer">
        {formatTime(timeLeft)}
      </div>
      
    </div>
  </div>
  