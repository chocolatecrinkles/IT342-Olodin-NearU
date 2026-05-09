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

    const displayPrice = (listing) => {
        if (!listing) return "N/A"

        if (listing.pricingType === "RANGE") {
            if (listing.minPrice != null && listing.maxPrice != null) {
            return `₱ ${listing.minPrice.toLocaleString()} - ${listing.maxPrice.toLocaleString()}`
            }
            return "N/A"
        }

        if (listing.price != null) {
            const base = `₱ ${listing.price.toLocaleString()}`

            if (listing.pricingType === "MONTHLY") return base + " / month"
            if (listing.pricingType === "WEEKLY") return base + " / week"

            return base
        }

        return "N/A"
    }

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
                            <p style={{ margin: 0, fontWeight: 'bold' }}>{displayPrice(l)}</p>
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