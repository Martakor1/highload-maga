import http from 'k6/http';
import { sleep } from 'k6';
export const options = {
    iterations: 40000,
};
// The default exported function is picked up by k6 as the entry point for the test script.
export default function () {
    // Create a random user
    const res = http.post('http://localhost:8080/users/random');
    sleep(0.1);
}
