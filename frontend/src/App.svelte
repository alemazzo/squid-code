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
    <Route path="/" component={Home} />
    <Route path="/problem/:id" component={Problem} />
    <Route path="/problem/:id/coding" component={Coding} />
    <Route path="/problem/:id/win" component={Win} />
    <Route path="/fail" component={Fail} />
  </main>
</Router>
