<script lang="ts">
  import { Router, Route, Link, navigate } from "svelte-routing";
  import Home from "./routes/Home.svelte";
  import "./app.css";
	import { onMount } from "svelte";
	import Problem from "./routes/Problem.svelte";
	import Fail from "./routes/Fail.svelte";
	import Coding from "./routes/Coding.svelte";
	import Win from "./routes/Win.svelte";

  export let url = "";

  onMount(() => {
 
  });
</script>

<Router {url}>
  <!-- Main Content -->
  <main class="main-content">
    <Route path="/squid-code/" component={Home} />
    <Route path="/squid-code/problem/:id" component={Problem} />
    <Route path="/squid-code/problem/:id/coding" component={Coding} />
    <Route path="/squid-code/problem/:id/win" component={Win} />
    <Route path="/squid-code/fail" component={Fail} />
  </main>
</Router>
