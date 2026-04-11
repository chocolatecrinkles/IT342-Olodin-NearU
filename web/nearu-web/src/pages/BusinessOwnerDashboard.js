import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import BusinessOwnerListings from "./BusinessOwnerListings";
import ListingDetail from "./ListingDetail";
import "./css/BusinessOwnerDashboard.css"; 

function BusinessOwnerDashboard() {
  const navigate = useNavigate();
  const [selectedListingId, setSelectedListingId] = useState(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <div className="dashboard-container">
      <header className="header">
        <div className="logo-text">NearU</div>
        <nav className="nav-links">
          <button className="nav-item active" onClick={() => navigate("/businessowner")}>Home</button>
          <button className="nav-item" onClick={() => navigate("/businessowner/list")}>Listings</button>
          <button className="nav-item">Profile</button>
          <div style={{ width: '1px', height: '20px', backgroundColor: '#999', margin: '0 5px' }}></div>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
        </nav>
      </header>

      <main className="main-content">
        <aside className="sidebar">
          {selectedListingId ? (
            <>
              <button 
                onClick={() => setSelectedListingId(null)}
                style={{ cursor: "pointer", background: "none", border: "none", color: "#4a5d79", fontWeight: "bold", width: "120px ", height: "30px" }}
              >
                ← Back to List
              </button>
              <ListingDetail id={selectedListingId} />
            </>
          ) : (
            <>
              <BusinessOwnerListings onSelectListing={setSelectedListingId} />
              <button 
                className="create-listing-btn" 
                onClick={() => navigate("/businessowner/add")}
              >
                + Create a Listing
              </button>
            </>
          )}
        </aside>

        <section className="map-view"></section>
      </main>
    </div>
  );
}

export default BusinessOwnerDashboard;