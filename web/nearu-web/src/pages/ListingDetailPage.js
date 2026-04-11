import { useParams, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import "./css/ListingDetailPage.css"

function ListingDetailPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [listing, setListing] = useState(null)
    const [images, setImages] = useState([])
    const [currentIndex, setCurrentIndex] = useState(0)

    useEffect(() => {
        fetch(`http://localhost:8080/api/listings`)
            .then(res => res.json())
            .then(data => {
                const found = data.find(l => l.id == id)
                setListing(found)
            })

        fetch(`http://localhost:8080/api/listings/${id}/images`)
            .then(res => res.json())
            .then(data => setImages(data))
    }, [id])

    if (!listing) return <p>Loading...</p>

    return (
        <div className="detail-layout">
            <header className="header">
                <div className="logo-text">NearU</div>
                <nav className="nav-group">
                    <button className="nav-item" onClick={() => navigate("/student")}>Home</button>
                    <button className="nav-item">Bookmarks</button>
                    <button className="nav-item">Profile</button>
                    <div className="nav-divider"></div>
                    <button className="logout-btn">Logout</button>
                </nav>
            </header>

            <div className="detail-container">
                <button onClick={() => navigate(-1)} style={{marginBottom: "20px", cursor: "pointer", background: "none", border: "none", color: "#4a5d79", fontWeight: "bold"}}>
                    ← Back
                </button>

                <div className="detail-header-row">
                    <span className="label-text">Listing Name</span>
                    <div className="title-underline">{listing.name}</div>
                </div>

                <div className="image-gallery-row">
                    <button className="nav-btn" onClick={() => setCurrentIndex(prev => prev === 0 ? images.length - 1 : prev - 1)}>◀</button>
                    
                    <div className="gallery-item">
                        {images.length > 0 ? (
                            <img src={`http://localhost:8080${images[currentIndex].imageUrl}`} alt="listing" />
                        ) : (
                            <svg width="50" height="50" viewBox="0 0 24 24" fill="#dee2e6"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
                        )}
                    </div>

                    <button className="nav-btn" onClick={() => setCurrentIndex(prev => prev === images.length - 1 ? 0 : prev + 1)}>▶</button>
                </div>

                <div className="info-section">
                    <span className="label-text">₱ Price</span>
                    <div className="underline-display">₱ {listing.price.toLocaleString()}</div>

                    <span className="label-text">Description</span>
                    <div className="underline-display">{listing.description}</div>

                    <span className="label-text">Address</span>
                    <div className="underline-display">{listing.address}</div>
                </div>

                <div className="map-placeholder">
                    <p>Map Location</p>
                </div>
            </div>
        </div>
    )
}

export default ListingDetailPage