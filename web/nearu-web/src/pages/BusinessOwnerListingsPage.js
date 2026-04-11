import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import "./css/MyListings.css"

const API = "http://localhost:8080/api/listings"

function OwnerListingsPage({ onSelectListing }) {
    const [listings, setListings] = useState([])
    const [editingId, setEditingId] = useState(null)
    const [editData, setEditData] = useState({})
    const [activeTab, setActiveTab] = useState("All")
    const navigate = useNavigate()

    const token = localStorage.getItem("token")

    const fetchListings = () => {
        fetch(`${API}/my`, {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        .then(res => res.json())
        .then(data => setListings(data.data || data))
    }

    useEffect(() => {
        fetchListings()
    }, [])

    const startEdit = (listing) => {
        setEditingId(listing.id)
        setEditData(listing)
    }

    const handleChange = (e) => {
        setEditData({
            ...editData,
            [e.target.name]: e.target.value
        })
    }

    const saveEdit = async () => {
        await fetch(`${API}/${editingId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(editData)
        })
        setEditingId(null)
        fetchListings()
    }

    return (
        <div className="mylistings-layout">
            <header className="header">
                <div className="logo-text">NearU</div>
                <nav className="nav-group">
                    <button className="nav-item" onClick={() => navigate("/businessowner")}>Home</button>
                    <button className="nav-item active">Listings</button>
                    <button className="nav-item">Profile</button>
                    <div style={{width: '1px', height: '20px', backgroundColor: '#ccc'}}></div>
                    <button className="logout-btn">Logout</button>
                </nav>
            </header>

            <main className="content-body">
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

                <div className="listings-grid">
                    {listings.map(l => (
                        <div key={l.id} className="listing-card" onClick={() => navigate(`/businessowner/list/${l.id}`)} style={{ cursor: "pointer" }}>
                            {editingId === l.id ? (
                                <div className="edit-form-overlay">
                                    <input className="edit-input" name="name" value={editData.name} onChange={handleChange} />
                                    <input className="edit-input" name="address" value={editData.address} onChange={handleChange} />
                                    <input className="edit-input" name="price" value={editData.price} onChange={handleChange} />
                                    <button className="save-btn" onClick={saveEdit}>Save</button>
                                </div>
                            ) : (
                                <>
                                    <div className="card-title">{l.name}</div>
                                    <div className="image-placeholder">
                                        <svg width="60" height="60" viewBox="0 0 24 24" fill="#cbd5e1"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
                                    </div>
                                    <div className="rating-info">
                                        Rated <b>4.5 ★</b> by 27 users
                                    </div>
                                    <button className="edit-trigger-btn" onClick={() => startEdit(l)}>Edit</button>
                                </>
                            )}
                        </div>
                    ))}
                </div>
            </main>
        </div>
    )
}

export default OwnerListingsPage;