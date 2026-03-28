import React from "react";
import { useNavigate } from "react-router-dom";
import "./css/BusinessOwnerDashboard.css"; 

function BusinessOwnerDashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  return (
    <div className="dashboard-container">
      {/* Header Section */}
      <header className="header">
        <div className="logo-text">NearU</div>
        <nav className="nav-links">
          <button className="nav-item active" onClick={() => navigate("/")}>Home</button>
          <button className="nav-item" onClick={() => navigate("/businessowner/list")}>Listings</button>
          <button className="nav-item">Profile</button>
          <div style={{ width: '1px', height: '20px', backgroundColor: '#999', margin: '0 5px' }}></div>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
        </nav>
      </header>

      {/* Body Section */}
      <main className="main-content">
        {/* Sidebar */}
        <aside className="sidebar">
          <p className="empty-state-text">You have no listing yet.</p>
          
          <button 
            className="create-listing-btn" 
            onClick={() => navigate("/businessowner/add")}
          >
            + Create a Listing
          </button>
        </aside>

        <section className="map-view">
        </section>
      </main>
    </div>
  );
}

export default BusinessOwnerDashboard;