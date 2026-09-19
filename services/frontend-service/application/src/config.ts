/**
 * Runtime configuration of the frontend.
 *
 * The values are injected at build time by Vite (`VITE_*` variables) so the
 * same source tree can be built for any environment (local, DO, k3s VPS, ...).
 * The fallbacks keep `npm run dev` working with no configuration at all.
 */
export const API_BASE_URL: string =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8000";

export const LEETCODE_BASE_URL: string =
  import.meta.env.VITE_LEETCODE_BASE_URL ?? "http://localhost:3000";
