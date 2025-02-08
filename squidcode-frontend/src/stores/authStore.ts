import { writable } from "svelte/store";

export interface AuthState {
    isAuthenticated: boolean;
    user: { name: string; email: string } | null;
    token: string | null;
}

export const authStore = writable<AuthState>({
    isAuthenticated: false,
    user: null,
    token: null,
});
