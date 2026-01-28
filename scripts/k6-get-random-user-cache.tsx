import http from 'k6/http';
import { sleep } from 'k6';
export const options = {
    iterations: 40000,
};
// The default exported function is picked up by k6 as the entry point for the test script.
export default function () {
    // Create a random user
    //http.post('http://localhost:8080/users/random');
    // Get all users
    const res = http.get('http://localhost:8080/users');
    let usrArray = [];
    try {
        if (res.status !== 200) {
            console.error('/users returned status', res.status);
        } else if (res.body && res.body.length > 0) {
            usrArray = JSON.parse(res.body);
        }
    } catch (e) {
        console.error('Failed to parse /users response:', e);
    }
    if (Array.isArray(usrArray) && usrArray.length > 0) {
        // Extract ids
        const ids = usrArray.map(u => u && u.id).filter(Boolean);
        if (ids.length > 0) {
            const id = ids[Math.floor(Math.random() * ids.length)];
            // Request single user by id
            http.get(`http://localhost:8080/users/${id}/1000000`);
        } else {
            console.warn('No ids found in usrArray');
        }
    } else {
        console.warn('/users returned empty or non-array');
    }
    // small sleep to avoid hammering too hard
    sleep(0.01);
}
