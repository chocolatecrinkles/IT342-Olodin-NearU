import { useState } from "react";
import "./css/AddListing.css";

const API = "http://localhost:8080/api/listings";

function AddListing() {
  const [listing, setListing] = useState({
    name: "",
    category: "",
    address: "",
    price: 0,
    latitude: 0,
    longitude: 0,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    setListing({
      ...listing,
      [name]: name === "price" ? Number(value) : value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(API, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        body: JSON.stringify(listing),
      });

      if (!response.ok) {
        const err = await response.text();
        throw new Error(err);
      }
      alert("Listing created!");
    } catch (err) {
      alert(err.message);
    }
  };

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

          <button type="submit" className="submit-btn">
            Add Listing
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddListing;