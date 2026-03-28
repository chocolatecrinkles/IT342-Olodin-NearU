import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { login } from "../services/authService";
import { jwtDecode } from "jwt-decode";
import "./css/Login.css";

export function getUserFromToken() {
  const token = localStorage.getItem("token");
  if (!token) return null;
  return jwtDecode(token);
}

function Login() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({
    email: "",
    password: ""
  });

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await login(credentials);
      localStorage.setItem("token", res.token);
      const user = getUserFromToken();

      if (user.role === "STUDENT") {
        navigate("/student");
      } else {
        navigate("/businessowner");
      }
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="login-layout">
      <header className="header">
        <div className="logo-text">NearU</div>
      </header>

      <main className="login-content">
        <div className="login-card">
          <h2>Login</h2>
          <form onSubmit={handleSubmit}>
            <input
              className="login-input"
              name="email"
              placeholder="Email"
              onChange={handleChange}
            />

            <input
              className="login-input"
              name="password"
              type="password"
              placeholder="Password"
              onChange={handleChange}
            />

            <button type="submit" className="login-btn">
              Login
            </button>

            <p className="register-text">
              Don't have an account?{" "}
              <span className="register-link" onClick={() => navigate("/register")}>
                Register
              </span>
            </p>
          </form>
        </div>
      </main>
    </div>
  );
}

export default Login;