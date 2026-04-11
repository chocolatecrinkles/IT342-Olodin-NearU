import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import "./css/Listings.css"

const API = "http://localhost:8080/api/listings"

function Listings({ search, filters }) {
    const navigate = useNavigate()
    const [listings, setListings] = useState([])
    const [images, setImages] = useState({})
    const token = localStorage.getItem("token")

    useEffect(() => {
        fetch(API)
            .then(res => res.json())
            .then(data => {
                setListings(data)
                data.forEach(l => {
                    fetch(`http://localhost:8080/api/listings/${l.id}/images`)
                        .then(res => res.json())
                        .then(imgs => {
                            setImages(prev => ({
                                ...prev,
                                [l.id]: imgs
                            }))
                        })
                })
            })
    }, [])

    const filtered = listings.filter(l => {
        const matchesSearch = l.name.toLowerCase().includes(search.toLowerCase())
        const matchesCategory = !filters?.categories?.length || filters.categories.includes(l.category)
        return matchesSearch && matchesCategory
    })

    const handleBookmark = async (e, listingId) => {
        e.stopPropagation() 
        try {
            await fetch("http://localhost:8080/api/bookmarks", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({ listingId })
            })
            alert("Bookmarked!")
        } catch (err) {
            alert("Failed to bookmark")
        }
    }

    return (
        <div className="listings-results-container">
            {filtered.map(l => (
                <div key={l.id} className="student-listing-card" onClick={() => navigate(`/listing/${l.id}`)}>
                    
                    <div className="card-header-row">
                        <h3 className="listing-title">{l.name}</h3>
                        <button className="bookmark-btn" onClick={(e) => handleBookmark(e, l.id)}>
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z" />
                            </svg>
                        </button>
                    </div>

                    <div className="card-image-box">
                        {images[l.id]?.length > 0 ? (
                            images[l.id].map(img => (
                                <img 
                                    key={img.id} 
                                    src={`http://localhost:8080${img.imageUrl}`} 
                                    alt="preview" 
                                />
                            ))
                        ) : (
                            <div style={{ margin: 'auto', color: '#999', fontSize: '10px' }}>No Images</div>
                        )}
                    </div>

                    <p className="listing-meta-text">{l.category} • {l.address}</p>

                    <div className="card-footer-row">
                        <span className="listing-price">₱ {l.price.toLocaleString()}</span>
                        <button className="view-details-btn">View</button>
                    </div>
                </div>
            ))}
        </div>
    )
}

export default Listings