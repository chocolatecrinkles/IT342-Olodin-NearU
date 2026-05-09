import { useState } from "react";
import "./css/AddListing.css";
import { useNavigate } from "react-router-dom"
import MapSelector from "./MapSelector"

const API = "http://localhost:8080/api/listings";

function AddListing() {
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

  const navigate = useNavigate();
  const [files, setFiles] = useState([]);
  const [listing, setListing] = useState({
    name: "",
    category: "",
    listingType:"",
    address: "",
    price: "",
    minPrice: "",
    maxPrice: "",
    pricingType: "",
    latitude: "",
    longitude: "",
    description: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    setListing({
      ...listing,
      [name]: name === "price" ? Number(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!listing.name || !listing.category || !listing.address || !listing.listingType || !listing.pricingType) {
      alert("Please fill required fields")
      return
    }

    if (listing.pricingType === "RANGE") {
      if (!listing.minPrice || !listing.maxPrice) {
        alert("Please enter price range")
        return
      }

      if (listing.minPrice > listing.maxPrice) {
        alert("Min price cannot be greater than max price")
        return
      }
    } else {
      if (!listing.price || listing.price <= 0) {
        alert("Price must be greater than 0")
        return
      }
    }

    const token = localStorage.getItem("token")

    try {
      const response = await fetch(API, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token,
        },
        body: JSON.stringify({
          ...listing,
          price: parseFloat(listing.price),
          latitude: listing.latitude ? parseFloat(listing.latitude) : null,
          longitude: listing.longitude ? parseFloat(listing.longitude) : null
        })
      })

      if (!response.ok) {
        const err = await response.text()
        throw new Error(err)
      }

      const createdListing = await response.json()

      if (files.length > 0) {
        const formData = new FormData()

        for (let i = 0; i < files.length; i++) {
          formData.append("files", files[i])
        }

        await fetch(`http://localhost:8080/api/listings/${createdListing.id}/images`, {
          method: "POST",
          headers: {
            Authorization: "Bearer " + token
          },
          body: formData
        })
      }

      alert("Listing created!")
      navigate("/businessowner")

    } catch (err) {
      alert(err.message)
    }
  }

  const formatText = (text) => {
    return text
      ?.toLowerCase()
      .replace("_", " ")
      .replace(/\b\w/g, c => c.toUpperCase())
  }

  return (
    <div className="add-listing-layout">
      <div className="layout-content-wrapper">
        <form onSubmit={handleSubmit} className="listing-form-container">
          <div className="listing-head">
            <input
              name="name"
              value={listing.name}
              placeholder="Name"
              onChange={handleChange}
              className="listing-name-input"
            />

            <select
              name="listingType"
              value={listing.listingType || ""}
              onChange={(e) => {
                const newType = e.target.value

                setListing(prev => ({
                  ...prev,
                  listingType: newType,
                  category: "",      
                  pricingType: "",
                  price: "",   
                }))
              }}
            >
              <option value="">Select Type</option>
              <option value="ACCOMMODATION">Accommodation</option>
              <option value="SERVICE">Service</option>
              <option value="OTHER">Other</option>
            </select>

            <select
              name="category"
              value={listing.category || ""}
              onChange={(e) => setListing({ ...listing, category: e.target.value })}
              disabled={!listing.listingType} 
            >
              <option value="">Select Category</option>

              {listing.listingType &&
                CATEGORY_OPTIONS[listing.listingType].map(cat => (
                  <option key={cat} value={cat}>
                    {formatText(cat)}
                  </option>
                ))
              }
            </select>
          </div>

          <input
            type="file"
            multiple
            accept="image/*"
            onChange={(e) => {
              const newFiles = Array.from(e.target.files)

              const updatedFiles = [...files, ...newFiles]

              if (updatedFiles.length > 15) {
                alert("Maximum 15 images only")
                return
              }

              setFiles(updatedFiles)
            }}
          />

          <div>
            {files.map((file, index) => (
              <div key={index}>
                <img src={URL.createObjectURL(file)} width="100" />
                
                <button type="button" onClick={() => {
                  const updated = files.filter((_, i) => i !== index)
                  setFiles(updated)
                }}>
                  Remove
                </button>
              </div>
            ))}
          </div>

          <div className="media-section">
            <div className="media-placeholder"></div>
            <div className="media-placeholder"></div>
            <div className="media-placeholder"></div>
          </div>

          <div className="form-group">

            <select
              name="pricingType"
              value={listing.pricingType || ""}
              onChange={(e) => {
                const newPricing = e.target.value

                setListing(prev => ({
                  ...prev,
                  pricingType: newPricing,
                  price: "",
                  minPrice: "",
                  maxPrice: ""
                }))
              }}
              disabled={!listing.listingType}
            >
              <option value="">Select Pricing</option>

              {listing.listingType &&
                PRICING_OPTIONS[listing.listingType].map(p => (
                  <option key={p} value={p}>
                    {formatText(p)}
                  </option>
                ))
              }
            </select>

            {listing.pricingType === "RANGE" ? (
              <div style={{ display: "flex", gap: "10px" }}>
                
                <input
                  type="number"
                  placeholder="Min Price"
                  value={listing.minPrice || ""}
                  onChange={(e) => setListing({
                    ...listing,
                    minPrice: Number(e.target.value)
                  })}
                />

                <input
                  type="number"
                  placeholder="Max Price"
                  value={listing.maxPrice || ""}
                  onChange={(e) => setListing({
                    ...listing,
                    maxPrice: Number(e.target.value)
                  })}
                />

              </div>
            ) : (
              <input
                type="number"
                placeholder={
                  listing.pricingType === "MONTHLY"
                    ? "Price per month"
                    : listing.pricingType === "WEEKLY"
                    ? "Price per week"
                    : "Enter price"
                }
                value={listing.price || ""}
                onChange={(e) => setListing({
                  ...listing,
                  price: Number(e.target.value)
                })}
                disabled={!listing.pricingType}
              />
            )}

          </div>

          <div className="form-group">
            <label>Address</label>
            <input
              name="address"
              value={listing.address}
              placeholder="Address"
              onChange={handleChange}
              className="address-input"
            />

            <label>Select Location</label>

            <MapSelector
              onSelect={(pos) => {
                setListing({
                  ...listing,
                  latitude: pos[0],
                  longitude: pos[1],
                });
              }}
            />

            <p style={{ fontSize: "12px", color: "#666" }}>
              Selected: {listing.latitude}, {listing.longitude}
            </p>
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              name="description"
              value={listing.description}
              placeholder="Description"
              onChange={handleChange}
              className="description-input"
            />
          </div>

          <button type="submit" className="submit-btn">
            Add Listing
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddListing;