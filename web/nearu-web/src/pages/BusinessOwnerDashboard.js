import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import BusinessOwnerListings from "./BusinessOwnerListings";
import ListingDetail from "./ListingDetail";
import "./css/BusinessOwnerDashboard.css";   
import MapView from "./MapView"

function BusinessOwnerDashboard() {
  const navigate = useNavigate();
  const [listings, setListings] = useState([]);
  const [selectedListingId, setSelectedListingId] = useState(null);

  useEffect(() => {
  fetch("http://localhost:8080/api/listings/my", {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token")
    }
  })
    .then(res => res.json())
    .then(data => {
      if (Array.isArray(data)) {
        setListings(data);
      } else if (Array.isArray(data.data)) {
        setListings(data.data);
      } else {
        setListings([]); // fallback
      }
    });
}, []);

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

        <section className="map-view">
          <MapView 
            listings={listings}
            selectedListingId={selectedListingId} 
          />
        </section>
      </main>
    </div>
  );
}

export default BusinessOwnerDashboard;