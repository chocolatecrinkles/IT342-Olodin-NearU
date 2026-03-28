import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/authService";
import "./css/Register.css";

function Register() {
  const navigate = useNavigate();

  const [user, setUser] = useState({
    firstname: "",
    lastname: "",
    email: "",
    password: "",
    role: ""
  });

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(user);
      navigate("/");
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="register-layout">
      <header className="header">
        <div className="logo-text">NearU</div>
      </header>

      <main className="register-content">
        <div className="register-card">
          <h2>Register</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <input
                className="register-input"
                name="firstname"
                placeholder="First Name"
                onChange={handleChange}
              />
              <input
                className="register-input"
                name="lastname"
                placeholder="Last Name"
                onChange={handleChange}
              />
            </div>

            <input
              className="register-input"
              name="email"
              placeholder="Email"
              onChange={handleChange}
            />

            <input
              className="register-input"
              name="password"
              type="password"
              placeholder="Password"
              onChange={handleChange}
            />

            <select
              className="register-select"
              name="role"
              onChange={handleChange}
            >
              <option value="">Select Role</option>
              <option value="STUDENT">Student</option>
              <option value="BUSINESS_OWNER">Business Owner</option>
            </select>

            <button type="submit" className="register-btn">
              Register
            </button>

            <p className="login-text">
              Already have an account?{" "}
              <span className="login-link" onClick={() => navigate("/")}>
                Login
              </span>
            </p>
          </form>
        </div>
      </main>
    </div>
  );
}

export default Register;