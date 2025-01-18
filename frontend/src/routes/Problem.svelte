<script>
  import { onMount } from "svelte";
  import { getLeetcodeInfo } from "../api/leetcode";
  import { navigate } from "svelte-routing";

  export let id;
  let problem = {};

  let original_timeLeft = 0; // 10 minutes in seconds
  let original_countdown = 30; // 30 seconds for countdown

  let interval;
  let modalVisible = true; // Control visibility of the modal
  let timeLeft = original_timeLeft;
  let countdown = original_countdown; // Countdown for modal (30 seconds)

  let timeForCode;

  const formatTime = (timeInSeconds) => {
    const minutes = Math.floor(timeInSeconds / 60);
    const seconds = timeInSeconds % 60;
    return `${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  };

  const secondsToMinutes = (timeInSeconds) => {
    return Math.floor(timeInSeconds / 60);
  };

  const secondsToMinutesSeconds = (timeInSeconds) => {
    let minutes = Math.floor(timeInSeconds / 60);
    let seconds = timeInSeconds % 60;
    if (minutes < 10) minutes = `0${minutes}`;
    if (seconds < 10) seconds = `0${seconds}`;
    return `${minutes}:${seconds}`;
  };

  const startTimer = () => {
    interval = setInterval(() => {
      if (timeLeft > 0) {
        timeLeft -= 1;
      } else {
        clearInterval(interval); // Stop the timer when it reaches 0
        window.open("https://leetcode.com/problems/" + id, "_blank");
        navigate(`/problem/${id}/coding`); // Redirect to the coding page
      }
    }, 1000);
  };

  const startCountdown = () => {
    const countdownInterval = setInterval(() => {
      if (countdown > 0) {
        countdown -= 1;
      } else {
        clearInterval(countdownInterval);
        modalVisible = false; // Hide the modal when countdown finishes
        startTimer(); // Start the main timer
      }
    }, 1000);
  };

  const skipCountdown = () => {
    clearInterval(countdown); // Stop the countdown
    modalVisible = false; // Hide the modal immediately
    startTimer(); // Start the main timer
  };

  onMount(async () => {
    try {
      problem = await getLeetcodeInfo(id);
      if (problem.difficulty === "Easy") {
        original_timeLeft = 10 * 60; //
        timeLeft = original_timeLeft;
        timeForCode = 15 * 60; //
      } else if (problem.difficulty === "Medium") {
        original_timeLeft = 15 * 60; //
        timeLeft = original_timeLeft;
        timeForCode = 30 * 60; //
      } else {
        original_timeLeft = 20 * 60; //
        timeLeft = original_timeLeft;
        timeForCode = 45 * 60; // 
      }
      console.log("LeetCode Problem: ", problem);
      startCountdown(); // Start the countdown when problem is fetched
    } catch (error) {
      console.error("Error fetching problem: ", error);
    }
  });

  const handleStartCoding = () => {
    console.log("Starting to code...");
    window.open("https://leetcode.com/problems/" + id, "_blank");
    navigate(`/problem/${id}/coding`); // Redirect to the coding page
  };
</script>


<style>
  /* Modal Container */
/* Modal Container */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
  z-index: 999;
}

/* Modal Content Styling */
.modal-content {
  background: #222;
  padding: 40px 60px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.7);
  text-align: center;
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  max-width: 1500px;
  height: auto;
}

/* Countdown styling */
.modal .countdown {
  font-size: 4rem;
  color: #f39c12;
  font-weight: bold;
  margin: 20px 0;
}

/* Info Text Styling */
.modal .info {
  font-size: 1rem;
  color: #f39c12;
  margin-top: 20px;
  font-weight: normal;
}

/* GIF Styling */
.modal .gif {
  max-width: 100%;
  height: auto;
  margin-top: 20px;
  display: block;
  /* Centering the GIF if you need additional control over its position */
  margin-left: auto;
  margin-right: auto;
}

/* Container for the main content */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  position: relative;
}


  /* Problem Section */
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
  }

  p {
    font-size: 1.2rem;
    margin-bottom: 20px;
    line-height: 1.6;
  }

  .description, .content {
    margin-top: 20px;
    font-size: 1.2rem;
    line-height: 1.6;
  }

  .description {
    max-height: 70vh;
    overflow-y: auto;
  }

  .content {
    margin-top: 30px;
  }

  img {
    max-width: 100%;
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
    font-size: 3rem;
    color: #f39c12;
    font-weight: bold;
    text-align: center;
  }

  /* Start Coding Button */
  .start-button {
    position: fixed;
    top: 130px;
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
    width: 200px;
  }

  .start-button:hover {
    background-color: #ff6347;
  }

  /* Skip Button */
.skip-button {
  background-color: #e74c3c;
  color: white;
  padding: 15px 30px;
  font-size: 1.2rem;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px; /* Adds space between the icon and text */
  width: 200px; /* Makes the button wider */
}

/* Hover Effect */
.skip-button:hover {
  background-color: #c0392b;
}

/* FontAwesome Icon */
.skip-button i {
  font-size: 1.5rem; /* Adjust the size of the icon */
}

</style>

<div class="container">
  <!-- Modal -->
  {#if modalVisible}
    <div class="modal">
      <div class="modal-content">
        <p>Get ready! Your interview will start in {original_countdown} seconds.</p>
        <img class="gif" src="https://media.giphy.com/media/pDgHg2Lcju3Ty/giphy.gif?cid=790b7611noyctyssiy1q495shqesx6tr1sa0jx2k1prtvzno&ep=v1_gifs_search&rid=giphy.gif&ct=g" alt="Trolling GIF" />
        <p class="countdown">{secondsToMinutesSeconds(countdown)}</p>
        
        <p class="info">
          You will now have {secondsToMinutes(original_timeLeft)} minutes to analyze the problem and brainstorm a solution (no coding yet).
        </p>
        <p class="info">
          Once the time is over, or you choose to start coding, the LeetCode problem will open. 
          Make sure your account is the same as the one entered here.
        </p>
        <p class="info">
          A timer of {secondsToMinutes(timeForCode)} minutes will start for you to solve the problem waiting for a submission to be found.
        </p>

        <!-- Skip button with Icon -->
        <button class="skip-button" on:click={skipCountdown}>
          Start Interview
        </button>
        
      </div>
    </div>
  {/if}

  <!-- Problem Content -->
  {#if !modalVisible}
    <div class="problem-section">
      <h1>{problem.title}</h1>
      <div class="description" >{@html problem.description}</div>
      <div class="content" >{@html problem.content}</div>
    </div>
  {/if}

  <!-- Timer -->
  <div class="timer-container">
    {formatTime(timeLeft)}
  </div>

  <!-- Start Coding Button -->
  <button class="start-button" on:click={handleStartCoding}>
    Code
  </button>
</div>
