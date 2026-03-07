import { useNavigate } from "react-router-dom"
import { useState } from "react"
import { login } from "../services/authService"

function Login() {
    const navigate = useNavigate()
    const [credentials, setCredentials] = useState({
        email: "",
        password: ""
    })

    const handleChange = (e) => {
        setCredentials({...credentials, [e.target.name]: e.target.value })
    }

    const handleSubmit = async (e) => {
        e.preventDefault();

        await login(credentials);

        alert("Login successful!");

        navigate("/dashboard");
    };

    return (
        <div>
            <h2>Login</h2>

            <form onSubmit={handleSubmit}>

                <input name="email" placeholder="Email" onChange={handleChange}/>
                <br/><br/>

                <input name="password" type="password" placeholder="Password" onChange={handleChange}/>
                <br/><br/>

                <button type="submit">Login</button>

                <p className="register-text">
                    Don't have an account? {""}
                    <span onClick={ () => navigate("/register")}>
                    Register
                    </span>
                </p>

            </form>
        </div>
    );
}

export default Login;