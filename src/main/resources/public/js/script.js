const usernameInput = document.querySelector('#username');
const passwordInput = document.querySelector('#password');
const loginButton = document.querySelector('#loginBtn');

const onLogin = async () => {
    // $
    debugger;

    const username = usernameInput.value;
    const password = passwordInput.value;

    const user = {
        username,
        password
    }

    try {
        const data = await fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(user)
        });

        const response = await data.json();

        // $
        debugger;

        if (response.ok) {
            console.log("Everything is working");

            window.location.href = '/security/helloService';
        } else {
            alert('Something went wrong');
        }

    } catch(err) {
        console.error(err);
    }
}

loginButton.addEventListener('click', () => onLogin());