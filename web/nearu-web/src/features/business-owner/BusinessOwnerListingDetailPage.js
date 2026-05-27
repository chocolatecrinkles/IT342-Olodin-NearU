import { useParams, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import { MapContainer, TileLayer, Marker } from "react-leaflet";
import MapSelector from "../map/MapSelector"
import "./css/BusinessOwnerListingDetailPage.css"

function BusinessOwnerListingDetailPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [listing, setListing] = useState(null)
    const [images, setImages] = useState([])
    const [isEditing, setIsEditing] = useState(false)
    const [editData, setEditData] = useState({})
    const CATEGORY_OPTIONS = {
        ACCOMMODATION: ["BOARDING_HOUSE", "DORM"],
        SERVICE: ["RESTAURANT", "CAFE", "LAUNDROMAT"],
        OTHER: ["OTHER"]
    }

    const PRICING_OPTIONS = {
        ACCOMMODATION: ["MONTHLY", "WEEKLY"],
        SERVICE: ["RANGE"],
        OTHER: ["RANGE"]
    }

    const [message, setMessage] = useState("")
    const [error, setError] = useState("")
    const token = localStorage.getItem("token")

    useEffect(() => {
        fetch(`http://localhost:8080/api/listings`)
            .then(res => res.json())
            .then(data => {
                const found = data.find(l => l.id == id)
                setListing(found)   
                setEditData({
                    ...found,
                    price: found.price ?? "",
                    minPrice: found.minPrice ?? "",
                    maxPrice: found.maxPrice ?? ""
                })
            })

        fetch(`http://localhost:8080/api/listings/${id}/images`)
            .then(res => res.json())
            .then(data => setImages(data))
    }, [id])

    const handleChange = (e) => {
        const { name, value } = e.target

        setEditData({
            ...editData,
            [name]: name === "price" ? Number(value) : value
        })
    }

    const saveEdit = async () => {
        setMessage("")
        setError("")

        if (
            !editData.name ||
            !editData.category ||
            !editData.address ||
            !editData.listingType ||
            !editData.pricingType
        ) {
            setError("Please fill all required fields")
            return
        }

        if (editData.pricingType === "RANGE") {
            if (!editData.minPrice || !editData.maxPrice) {
                setError("Please enter price range")
                return
            }

            if (editData.minPrice > editData.maxPrice) {
                setError("Min price cannot be greater than max price")
                return
            }
        } else {
            if (!editData.price || editData.price <= 0) {
                setError("Price must be greater than 0")
                return
            }
        }

        if (!editData.latitude || !editData.longitude) {
            setError("Please select a location on the map")
            return
        }

        try {
            const res = await fetch(`http://localhost:8080/api/listings/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(editData)
            })

            const data = await res.json()

            if (!res.ok) {
                setError(data.message || "Failed to update listing")
                return
            }

            setListing(data)
            setEditData(data)
            setIsEditing(false)

            setMessage("Listing updated successfully!")

        } catch (err) {
            setError("Network error. Please try again.")
        }
    }
    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/");
    };

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

    const formatText = (text) => {
        return text
            ?.toLowerCase()
            .replace("_", " ")
            .replace(/\b\w/g, c => c.toUpperCase())
    }

    if (!listing) return <p>Loading...</p>

    return (
        <div className="detail-layout">
            <header className="header">
                <div className="logo-text">NearU</div>
                <nav className="nav-group">
                    <button className="nav-item" onClick={() => navigate("/businessowner")}>Home</button>
                    <button className="nav-item active">Listings</button>
                    <button className="nav-item">Profile</button>
                    <div className="nav-divider"></div>
                    <button className="logout-btn" onClick={() => handleLogout}>Logout</button>
                </nav>
            </header>

            <button onClick={() => navigate("/businessowner/list")}>Back to Listings</button>

            <div className="detail-container">

                {message && <div className="success-msg">{message}</div>}
                {error && <div className="error-msg">{error}</div>}

                <div className="detail-header-row">
                    <span className="label-text" style={{margin:0}}>Listing Name</span>
                    <div className="title-underline">
                        {isEditing ? (
                            <input name="name" className="underline-field" style={{marginBottom:0}} value={editData.name} onChange={handleChange}/>
                        ) : listing.name}
                    </div>
                </div>

                <div className="image-gallery-row">
                    {[0, 1, 2].map(idx => (
                        <div key={idx} className="gallery-item">
                            {images[idx] ? (
                                <img src={`http://localhost:8080${images[idx].imageUrl}`} alt="preview" />
                            ) : (
                                <svg width="50" height="50" viewBox="0 0 24 24" fill="#dee2e6"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
                            )}
                        </div>
                    ))}
                </div>

                <div className="info-section">
                    <span className="label-text">Type</span>
                    {isEditing ? (
                        <select
                            value={editData.listingType || ""}
                            onChange={(e) => {
                            const newType = e.target.value
                            setEditData(prev => ({
                                ...prev,
                                listingType: newType,
                                category: "",
                                pricingType: "",
                                price: "",
                                minPrice: "",
                                maxPrice: ""
                            }))
                            }}
                        >
                            <option value="">Select Type</option>
                            <option value="ACCOMMODATION">Accommodation</option>
                            <option value="SERVICE">Service</option>
                            <option value="OTHER">Other</option>
                        </select>
                    ) : (
                        <div>{formatText(listing.listingType)}</div>
                    )}

                    <span className="label-text">Category</span>
                    {isEditing ? (
                        <select
                            value={editData.category || ""}
                            onChange={(e) =>
                            setEditData({ ...editData, category: e.target.value })
                            }
                            disabled={!editData.listingType}
                        >
                            <option value="">Select Category</option>
                            {editData.listingType &&
                            CATEGORY_OPTIONS[editData.listingType].map(cat => (
                                <option key={cat} value={cat}>
                                {formatText(cat)}
                                </option>
                            ))}
                        </select>
                    ) : (
                        <div>{formatText(listing.category)}</div>
                    )}

                    <span className="label-text">Price</span>
                    {isEditing ? (
                        <>
                            <select
                            value={editData.pricingType || ""}
                            onChange={(e) => {
                                const newPricing = e.target.value
                                setEditData(prev => ({
                                ...prev,
                                pricingType: newPricing,
                                price: "",
                                minPrice: "",
                                maxPrice: ""
                                }))
                            }}
                            disabled={!editData.listingType}
                            >
                            <option value="">Select Pricing</option>
                            {editData.listingType &&
                                PRICING_OPTIONS[editData.listingType].map(p => (
                                <option key={p} value={p}>
                                    {formatText(p)}
                                </option>
                                ))}
                            </select>

                            {editData.pricingType === "RANGE" ? (
                                <div style={{ display: "flex", gap: "10px" }}>
                                    <input
                                    type="number"
                                    placeholder="Min Price"
                                    value={editData.minPrice || ""}
                                    onChange={(e) =>
                                        setEditData({ ...editData, minPrice: e.target.value === "" ? "" : Number(e.target.value) })
                                    }
                                    />

                                    <input
                                    type="number"
                                    placeholder="Max Price"
                                    value={editData.maxPrice || ""}
                                    onChange={(e) =>
                                        setEditData({ ...editData, maxPrice: e.target.value === "" ? "" : Number(e.target.value) })
                                    }
                                    />
                                </div>
                            ) : (
                                <input
                                    type="number"
                                    value={editData.price || ""}
                                    onChange={(e) =>
                                    setEditData({ ...editData, price: e.target.value === "" ? "" : Number(e.target.value) })
                                    }
                                    placeholder="Enter price"
                                    disabled={!editData.pricingType}
                                />
                            )}
                        </>
                    ) : (
                        <div className="underline-field">{displayPrice(listing)}</div>
                    )}

                    <span className="label-text">Description</span>
                    {isEditing ? (
                        <textarea name="description" className="underline-field" value={editData.description} onChange={handleChange} style={{height: '60px'}}/>
                    ) : <div className="underline-field">{listing.description}</div>}

                    <span className="label-text">Address</span>
                    {isEditing ? (
                        <input name="address" className="underline-field" value={editData.address} onChange={handleChange}/>
                    ) : <div className="underline-field">{listing.address}</div>}

                    <div className="map-section">
                        <span className="label-text">Location</span>

                        {isEditing ? (
                            <MapSelector
                                initialPosition={
                                editData.latitude && editData.longitude
                                    ? [editData.latitude, editData.longitude]
                                    : null
                                }
                                onSelect={(pos) => {
                                setEditData({
                                    ...editData,
                                    latitude: pos[0],
                                    longitude: pos[1],
                                });
                                }}
                            />
                        ) : (
                            listing.latitude && listing.longitude ? (
                                <MapContainer
                                center={[listing.latitude, listing.longitude]}
                                zoom={15}
                                style={{ height: "250px", width: "100%", borderRadius: "8px" }}
                                >
                                <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
                                <Marker position={[listing.latitude, listing.longitude]} />
                                </MapContainer>
                            ) : (
                                <p>No location set</p>
                            )
                        )}
                    </div>
                </div>

                <div className="action-footer">
                    {isEditing ? (
                        <>
                            <button className="edit-save-btn" onClick={saveEdit}>Save Changes</button>
                            <button className="edit-save-btn" style={{backgroundColor: '#ccc'}} 
                                onClick={() => {
                                    setEditData(listing) 
                                    setIsEditing(false)
                                    }}
                            >
                                Cancel
                            </button>
                        </>
                    ) : (
                        <button className="edit-save-btn" onClick={() => setIsEditing(true)}>Edit Listing</button>
                    )}
                </div>
            </div>
        </div>
    )
}

export default BusinessOwnerListingDetailPage;