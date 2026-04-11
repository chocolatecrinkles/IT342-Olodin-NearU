import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import "./css/Bookmarks.css"

const API = "http://localhost:8080/api/bookmarks"

function Bookmarks() {
    const [bookmarks, setBookmarks] = useState([])
    const [listings, setListings] = useState({})
    const [activeTab, setActiveTab] = useState("All")
    const token = localStorage.getItem("token")
    const navigate = useNavigate()

    useEffect(() => {
        fetch(API, {
            headers: { Authorization: "Bearer " + token }
        })
        .then(res => res.json())
        .then(data => setBookmarks(data))

        fetch("http://localhost:8080/api/listings")
            .then(res => res.json())
            .then(allListings => {
                const map = {}
                allListings.forEach(l => { map[l.id] = l })
                setListings(map)
            })
    }, [token])

    const handleLogout = () => {
        localStorage.removeItem("token")
        navigate("/")
    }

    return (
        <div className="bookmarks-layout">
            <header className="header">
                <div className="logo-text">NearU</div>
                <nav className="nav-group">
                    <button className="nav-item" onClick={() => navigate("/student")}>Home</button>
                    <button className="nav-item active">Bookmarks</button>
                    <button className="nav-item">Profile</button>
                    <div className="nav-divider"></div>
                    <button className="logout-btn" onClick={handleLogout}>Logout</button>
                </nav>
            </header>

            <main className="bookmarks-content">
                <div className="filter-tabs">
                    {["All", "Accomodation", "Services"].map(tab => (
                        <div 
                            key={tab} 
                            className={`tab ${activeTab === tab ? 'active' : ''}`}
                            onClick={() => setActiveTab(tab)}
                        >
                            {tab}
                        </div>
                    ))}
                </div>

                <div className="bookmarks-grid">
                    {bookmarks.map(b => {
                        const listing = listings[b.listingId]
                        if (!listing) return null

                        return (
                            <div 
                                key={b.id} 
                                className="bookmark-card" 
                                onClick={() => navigate(`/listing/${b.listingId}`)}
                            >
                                <div className="card-header">
                                    <h3 className="listing-name">{listing.name}</h3>
                                    {/* The Gray Category Box */}
                                    <div className="category-box">{listing.category}</div>
                                </div>

                                <div className="card-image-placeholder">
                                    <svg width="40" height="40" viewBox="0 0 24 24" fill="#dee2e6">
                                        <path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/>
                                    </svg>
                                </div>

                                <p className="price-text">
                                    ₱ {listing.price.toLocaleString()} 
                                    <span className="price-subtext"> / month</span>
                                </p>
                            </div>
                        )
                    })}
                </div>
            </main>
        </div>
    )
}

export default Bookmarks