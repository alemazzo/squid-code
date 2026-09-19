<script lang="ts">
  import { navigate } from "./index";

  /** Target path, e.g. `/problem/two-sum`. */
  export let to: string;
  export let replace = false;

  function handleClick(event: MouseEvent): void {
    // Let the browser handle new tab/window, middle click and modified clicks.
    if (event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
      return;
    }
    event.preventDefault();
    navigate(to, { replace });
  }
</script>

<a href={to} on:click={handleClick} {...$$restProps}>
  <slot />
</a>
