import { useNavigate } from "react-router-dom";

function SelectRole() {
  const navigate = useNavigate();

  const chooseRole = async (role) => {
    const token = localStorage.getItem("token");

    await fetch("http://localhost:8080/api/auth/set-role", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
      },
      body: JSON.stringify({ role })
    });

    if (role === "STUDENT") {
      navigate("/student");
    } else {
      navigate("/businessowner");
    }
  };

  return (
    <div>
      <h2>Select your role</h2>
      <button onClick={() => chooseRole("STUDENT")}>Student</button>
      <button onClick={() => chooseRole("BUSINESS_OWNER")}>Business Owner</button>
    </div>
  );
}

export default SelectRole;