// Config
const port = 4567;
let BASE_URL = `https://${window.location.hostname}:${port}`;

const usernameInput = document.querySelector('#username');
const passwordInput = document.querySelector('#password');
const loginButton = document.querySelector('#loginBtn');

const onLogin = async (event) => {
    event.preventDefault();
    // $
    debugger;

    const username = usernameInput.value;
    const password = passwordInput.value;

    const user = {
        username,
        password
    }

    try {
        const data = await fetch(`${BASE_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(user)
        });

        const response = await data.json();

        // $
        debugger;

        if (response.status === 200) {
            console.log("Everything is working");

            window.location.href = '/security/helloService';
        } else {
            alert('Something went wrong');
        }

    } catch(err) {
        console.error(err);
    }
}

loginButton.addEventListener('click', (e) => onLogin(e));