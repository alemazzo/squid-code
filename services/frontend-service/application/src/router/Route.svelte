<script lang="ts">
  import type { Component } from "svelte";
  import { pathname, matchPath } from "./index";

  /** Route pattern, e.g. `/problem/:id`. */
  export let path: string;
  /** Component rendered when the pattern matches. */
  export let component: Component<any>;

  $: params = matchPath(path, $pathname);
</script>

{#if params}
  {@const RoutedComponent = component}
  <RoutedComponent {...params} />
{/if}
