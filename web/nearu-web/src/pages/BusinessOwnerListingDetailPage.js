import { useParams, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import "./css/BusinessOwnerListingDetailPage.css"

function BusinessOwnerListingDetailPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [listing, setListing] = useState(null)
    const [images, setImages] = useState([])
    const [isEditing, setIsEditing] = useState(false)
    const [editData, setEditData] = useState({})
    const token = localStorage.getItem("token")

    useEffect(() => {
        fetch(`http://localhost:8080/api/listings`)
            .then(res => res.json())
            .then(data => {
                const found = data.find(l => l.id == id)
                setListing(found)   
                setEditData(found)
            })

        fetch(`http://localhost:8080/api/listings/${id}/images`)
            .then(res => res.json())
            .then(data => setImages(data))
    }, [id])

    const handleChange = (e) => {
        setEditData({ ...editData, [e.target.name]: e.target.value })
    }

    const saveEdit = async () => {
        await fetch(`http://localhost:8080/api/listings/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(editData)
        })
        setListing(editData)
        setIsEditing(false)
        alert("Listing Successfully edited.")
    }

    if (!listing) return <p>Loading...</p>

    return (
        <div className="detail-layout">
            <header className="header">
                <div className="logo-text">NearU</div>
                <nav className="nav-group">
                    <button className="nav-item" onClick={() => navigate("/business")}>Home</button>
                    <button className="nav-item active">Listings</button>
                    <button className="nav-item">Profile</button>
                    <div className="nav-divider"></div>
                    <button className="logout-btn">Logout</button>
                </nav>
            </header>

            <div className="detail-container">
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
                    <span className="label-text">Price</span>
                    {isEditing ? (
                        <input name="price" className="underline-field" value={editData.price} onChange={handleChange}/>
                    ) : <div className="underline-field">₱ {listing.price.toLocaleString()}</div>}

                    <span className="label-text">Description</span>
                    {isEditing ? (
                        <textarea name="description" className="underline-field" value={editData.description} onChange={handleChange} style={{height: '60px'}}/>
                    ) : <div className="underline-field">{listing.description}</div>}

                    <span className="label-text">Address</span>
                    {isEditing ? (
                        <input name="address" className="underline-field" value={editData.address} onChange={handleChange}/>
                    ) : <div className="underline-field">{listing.address}</div>}
                </div>

                <div className="action-footer">
                    {isEditing ? (
                        <>
                            <button className="edit-save-btn" onClick={saveEdit}>Save Changes</button>
                            <button className="edit-save-btn" style={{backgroundColor: '#ccc'}} onClick={() => setIsEditing(false)}>Cancel</button>
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