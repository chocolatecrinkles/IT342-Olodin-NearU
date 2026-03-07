import { useState } from "react";
import { useNavigate } from "react-router-dom"
import { register } from "../services/authService";

function Register() {
    const navigate = useNavigate()

    const [user, setUser] = useState({
        firstname: "",
        lastname: "",
        email: "",
        password: ""
    });

    const handleChange = (e) => {
        setUser({...user, [e.target.name]: e.target.value});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await register(user);
        alert("Registration successful!");
    };

    return (
        <div>
            <h2>Register</h2>

            <form onSubmit={handleSubmit}>

                <input name="firstname" placeholder="First Name" onChange={handleChange}/>
                <br/><br/>

                <input name="lastname" placeholder="Last Name" onChange={handleChange}/>
                <br/><br/>

                <input name="email" placeholder="Email" onChange={handleChange}/>
                <br/><br/>

                <input name="password" type="password" placeholder="Password" onChange={handleChange}/>
                <br/><br/>

                <button type="submit">Register</button>

                <p className="login-text">
                    Already have an account? {""}
                    <span onClick={ () => navigate("/")}>
                    Login
                    </span>
                </p>

            </form>
        </div>
    );
}

export default Register;