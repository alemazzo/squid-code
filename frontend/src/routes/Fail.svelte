<script>
  
  import { navigate } from 'svelte-routing';

  function home() {
    navigate('/squid-code');
  }
  
  // This component doesn't require logic, just a funny message for the "You Failed" page
  </script>

  
  <style>
    /* Global Styles */
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
  
    body {
      font-family: 'Arial', sans-serif;
      background-color: #1e1e1e;
      color: white;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      overflow-x: hidden;
      padding: 0;
      text-align: center;
    }
  
    /* Main Container */
    .fail-container {
      background: #222;
      padding: 40px;
      width: 70%;
      box-shadow: 0 0 20px rgba(255, 0, 0, 0.7);
      text-align: center;
      border-radius: 20px;
    }
  
    /* Fail Title */
    .fail-title {
      font-size: 3rem;
      margin-bottom: 20px;
      font-weight: bold;
      color: #f39c12;
      text-transform: uppercase;
      letter-spacing: 2px;
      text-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
    }
  
    /* Fail Message */
    .fail-message {
      font-size: 2rem;
      color: #fff;
      margin-bottom: 30px;
      font-weight: 500;
      line-height: 1.6;
      text-shadow: 0 0 5px rgba(255, 0, 0, 0.7);
    }
  
    /* Bully Message */
    .bully-message {
      font-size: 1.5rem;
      color: #ff6347;
      font-weight: 600;
      margin-top: 20px;
      text-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
    }
  
    .bully-message span {
      color: #f39c12;
      font-weight: 700;
    }
  
    /* Try Again Button */
    .try-again-button {
      background-color: #f39c12;
      color: #1e1e1e;
      padding: 20px 40px;
      font-size: 1.5rem;
      font-weight: bold;
      border: none;
      border-radius: 10px;
      box-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
      cursor: pointer;
      transition: background-color 0.3s ease;
      width: 220px;
      text-align: center;
      margin-top: 30px;
    }
  
    .try-again-button:hover {
      background-color: #ff6347;
    }
  
    /* Fail Image */
    .fail-image {
      margin-top: 20px;
      width: 200px;
      height: auto;
      opacity: 0.8;
    }

    .fail-image-container {
      display: flex;
      justify-content: center;
    }

  </style>
  
  <div class="fail-container">
    <div class="fail-title">
        YOU FAILED! 😡
      </div>
    
      <div class="fail-message">
        Looks like you bombed this one...<br />
        You're not getting that job. Better luck somewhere else. 😤
      </div>
    
      <div class="angry-message">
        <span>Next time, try harder!</span><br />
        This isn't a game. You blew your chance! 😡
      </div>
    
      <!-- Optionally add a humorous fail image -->
      <!-- svelte-ignore a11y_img_redundant_alt -->
       <div class="fail-image-container">
         <img class="fail-image" src="https://media.giphy.com/media/fAnEC88LccN7a/giphy.gif" alt="Fail Image" />
         </div>
    
      <button class="try-again-button" on:click={home}>
        Try Again... If You Dare
      </button>
  </div>
  