<script lang="ts">
  import { Router, Route, Link, navigate } from "svelte-routing";
  import Home from "./routes/Home.svelte";
  import "./app.css";
	import { onMount } from "svelte";

  export let url = "";

  onMount(() => {
 
  });
</script>

<Router {url}>
  <!-- Main Content -->
  <main class="main-content">
    <Route path="/" component={Home} />
  </main>
</Router>
