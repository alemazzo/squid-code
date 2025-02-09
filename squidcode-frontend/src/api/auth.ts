import { authStore } from "../stores/authStore";
import { navigate } from "svelte-routing";

const baseUrl = 'https://api.squidcode.com';

// On load, check if in the localStorage there is a JWT
export function initAuth(): void {
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
        authStore.set({
            isAuthenticated: true,
            user: null,
            token: jwt,
        });
    }
}

// Login with the Google token
export async function loginWithGoogle(googleResponse: any): Promise<void> {
    try {
        const token = googleResponse.credentials
        // Send the token to the backend to verify and generate the JWT
        const res = await fetch(`${baseUrl}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ token }),
        });

        const data = await res.text();
        
        if (data) {
            // Save JWT in localStorage
            localStorage.setItem('jwt', data);
            
            // Update auth store with the user's information
            authStore.set({
                isAuthenticated: true,
                token: data,
                user: null,
            });

            // Redirect to the dashboard or desired page after login
            navigate('/');
        } else {
            console.error('Login failed: ', data);
        }
    } catch (error) {
        console.error('Error during login:', error);
    }
}

// Logout function to clear authentication state
export async function logout(): Promise<void> {
    authStore.set({
        isAuthenticated: false,
        user: null,
        token: null,
    });
    localStorage.removeItem('jwt');
}
