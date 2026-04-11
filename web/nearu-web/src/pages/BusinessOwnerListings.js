import { useEffect, useState } from "react";

function BusinessOwnerListings({ onSelectListing }) {
    const [listings, setListings] = useState([]);
    const token = localStorage.getItem("token");

    useEffect(() => {
        fetch("http://localhost:8080/api/listings/my", {
            headers: { Authorization: "Bearer " + token }
        })
        .then(res => res.json())
        .then(data => setListings(data))
        .catch(err => console.error(err));
    }, [token]);

    return (
        <div className="sidebar-list-container">
            {listings.length === 0 ? (
                <p className="empty-state-text">No listings found.</p>
            ) : (
                listings.map(l => (
                    <div 
                        key={l.id} 
                        className="owner-listing-card"
                        onClick={() => onSelectListing(l.id)}
                    >
                        <h3 className="card-title">{l.name}</h3>
                        <div className="card-img-placeholder"></div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <p style={{ margin: 0, fontWeight: 'bold' }}>₱ {l.price.toLocaleString()}</p>
                            <button style={{ background: 'white', border: '1px solid #ccc', padding: '4px 12px', borderRadius: '4px', fontSize: '12px', cursor: 'pointer' }}>
                                Edit
                            </button>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

export default BusinessOwnerListings;