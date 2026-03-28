import { useNavigate } from "react-router-dom"

function Dashboard(){
    const navigate = useNavigate()

    const handleLogout = () => {
        localStorage.removeItem("user")
        navigate("/")
    }

    return(
        <div>
            <h2>Student Dashboard</h2>
            <p>Login successful.</p>

            <button onClick={ handleLogout }>Logout</button>
        </div>
    );
}

export default Dashboard;