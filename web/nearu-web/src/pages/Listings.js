import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import "./css/Listings.css"

const API = "http://localhost:8080/api/listings"

function Listings({ search, filters, setParentListings, onSelectListing }) {
    const navigate = useNavigate()
    const [listings, setListings] = useState([])
    const [images, setImages] = useState({})
    const token = localStorage.getItem("token")
    const [bookmarksMap, setBookmarksMap] = useState({})

    useEffect(() => {
        fetch(API)
            .then(res => res.json())
            .then(data => {
                setListings(data)

                if (setParentListings) {
                    setParentListings(data) 
                }
                
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
        
            fetch("http://localhost:8080/api/bookmarks", {
                headers: { Authorization: "Bearer " + token }
            })
            .then(res => res.json())
            .then(data => {
                const map = {}
                data.forEach(b => {
                    map[b.listingId] = b.id
                })
                setBookmarksMap(map)
            })
    }, [])

    const filtered = listings.filter(l => {
        if (!l) return false
        const searchText = (search || "").toLowerCase()
        const matchesSearch = (l.name?.toLowerCase() || "").includes(searchText) || 
                             (l.address?.toLowerCase() || "").includes(searchText)
        const matchesCategory = !filters?.categories?.length || filters.categories.includes(l.category)
        return matchesSearch && matchesCategory
    })

    const handleBookmark = async (e, listingId) => {
        e.stopPropagation()

        try {
            if (bookmarksMap[listingId]) {
                const bookmarkId = bookmarksMap[listingId]

                await fetch(`http://localhost:8080/api/bookmarks/${bookmarkId}`, {
                    method: "DELETE",
                    headers: { Authorization: "Bearer " + token }
                })

                setBookmarksMap(prev => {
                    const updated = { ...prev }
                    delete updated[listingId]
                    return updated
                })

            } else {
                const res = await fetch("http://localhost:8080/api/bookmarks", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({ listingId })
                })

                const data = await res.json()

                setBookmarksMap(prev => ({
                    ...prev,
                    [listingId]: data.id 
                }))
            }

        } catch (err) {
            console.error("Bookmark error:", err)
        }
    }
    
    const displayPrice = (listing) => {
        if (listing.pricingType === "RANGE") {
            return `₱ ${listing.minPrice} - ${listing.maxPrice}`
        }

        if (listing.pricingType === "MONTHLY") {
            return `₱ ${listing.price} / month`
        }

        if (listing.pricingType === "WEEKLY") {
            return `₱ ${listing.price} / week`
        }

        return "N/A"
    }

    const formatText = (text) => {
        return text
            .replace("_", " ")
            .replace(/\b\w/g, c => c.toUpperCase())
    }

    return (
        <div className="listings-results-container">
            {filtered.map(l => (
                <div key={l.id} className="student-listing-card" onClick={() => onSelectListing(l.id)}>
                    
                    <div className="card-header-row">
                        <h3 className="listing-title">{l.name}</h3>
                        <button className="bookmark-btn" onClick={(e) => handleBookmark(e, l.id)}>
                            <svg width="20" height="20" viewBox="0 0 24 24" fill={bookmarksMap[l.id] ? "currentColor" : "none"}  stroke="currentColor" strokeWidth="2">
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

                    <p className="listing-meta-text">{formatText(l.category)} • {l.address}</p>

                    <div className="card-footer-row">
                        <span className="listing-price">{displayPrice(l)}</span>
                        <button 
                            className="view-details-btn"
                            onClick={(e) => {
                                e.stopPropagation()
                                onSelectListing(l.id)
                            }}
                            >
                            View
                            </button>
                    </div>
                </div>
            ))}
        </div>
    )
}

export default Listings