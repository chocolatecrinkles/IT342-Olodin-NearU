import { useState } from "react";
import "./css/AddListing.css";
import { useNavigate } from "react-router-dom"

const API = "http://localhost:8080/api/listings";

function AddListing() {
  const navigate = useNavigate();
  const [files, setFiles] = useState([]);
  const [listing, setListing] = useState({
    name: "",
    category: "",
    listingType:"",
    address: "",
    price: "",
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

    if (!listing.name || !listing.category || !listing.address || !listing.price || !listing.listingType) {
      alert("Please fill required fields")
      return
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

  return (
    <div className="add-listing-layout">
      <div className="layout-content-wrapper">
        <form onSubmit={handleSubmit} className="listing-form-container">
          <div className="listing-head">
            <input
              name="name"
              placeholder="Name"
              onChange={handleChange}
              className="listing-name-input"
            />

            <select
              name="listingType"
              onChange={handleChange}
              className="listing-type-select"
            >
              <option value="">Select type</option>
              <option value="ACCOMMODATION">Accommodation</option>
              <option value="SERVICE">Service</option>
            </select>

            <select
              name="category"
              onChange={handleChange}
              className="listing-category-select"
            >
              <option value="">Select category</option>
              <option value="BOARDING_HOUSE">Boarding House</option>
              <option value="DORM">Dorm</option>
              <option value="RESTAURANT">Restaurant</option>
              <option value="CAFE">Cafe</option>
              <option value="LAUNDROMAT">Laundromat</option>
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
            <label>Price</label>
            <div className="price-input-wrapper">
              <input
                name="price"
                type="number"
                placeholder="00,000"
                onChange={handleChange}
                className="price-input"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Address</label>
            <input
              name="address"
              placeholder="Address"
              onChange={handleChange}
              className="address-input"
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              name="description"
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