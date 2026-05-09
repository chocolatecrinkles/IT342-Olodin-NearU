import { useState } from "react"
import Listings from "./Listings"
import { useNavigate } from "react-router-dom"
import "./css/StudentDashboard.css"
import ListingDetail from "./ListingDetail"
import MapView from "./MapView"

function StudentDashboard() {
  const navigate = useNavigate()
  const [search, setSearch] = useState("")
  const [selectedListingId, setSelectedListingId] = useState(null)
  const [listings, setListings] = useState([]);
  const [filters, setFilters] = useState({
    categories: [],
    minPrice: "",
    maxPrice: ""
  })

  // Generate price options from 500 to 15000
  const priceOptions = [];
  for (let i = 0; i <= 29; i++) {
    priceOptions.push(500 + i * 500);
  }

  const handleLogout = () => {
    localStorage.removeItem("token")
    navigate("/")
  }

  const handleCategoryChange = (category) => {
    setFilters(prev => {
      const isSelected = prev.categories.includes(category)
      return {
        ...prev,
        categories: isSelected 
          ? prev.categories.filter(c => c !== category) 
          : [...prev.categories, category]
      }
    })
  }

  const handlePriceChange = (type, value) => {
    setFilters(prev => ({
      ...prev,
      [type]: value
    }))
  }

  const filteredListings = listings.filter(l => {
    if (!l) return false // ✅ skip invalid items

    const name = l.name?.toLowerCase() || ""
    const address = l.address?.toLowerCase() || ""
    const searchText = search?.toLowerCase() || ""

    const matchesSearch = name.includes(searchText) || address.includes(searchText)

    const matchesCategory =
      !filters.categories.length || filters.categories.includes(l.category)

    const matchesMinPrice = !filters.minPrice || filters.minPrice === "" || 
      (l.pricingType === "RANGE" ? (l.minPrice >= Number(filters.minPrice)) : (l.price >= Number(filters.minPrice)))

    const matchesMaxPrice = !filters.maxPrice || filters.maxPrice === "" || 
      (l.pricingType === "RANGE" ? (l.maxPrice <= Number(filters.maxPrice)) : (l.price <= Number(filters.maxPrice)))

    return matchesSearch && matchesCategory && matchesMinPrice && matchesMaxPrice
  })

  return (
    <div className="student-layout">
      <header className="header">
        <div className="logo-text">NearU</div>
        <nav className="nav-group">
          <button className="nav-item">Home</button>
          <button className="nav-item" onClick={() => navigate("/bookmarks")}>Bookmarks</button>
          <button className="nav-item">Profile</button>
          <div className="nav-divider"></div>
          <button className="logout-btn" onClick={handleLogout}>Logout</button>
        </nav>
      </header>

      <main className="main-content">
        <aside className="sidebar">
          {!selectedListingId && (
            <>
              <div className="search-bar-container">
                <input
                  className="search-input"
                  type="text"
                  placeholder="Search"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#666" strokeWidth="2">
                  <path d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>

            
<div className="filter-tags-container">
                {["BOARDING_HOUSE", "DORM", "RESTAURANT", "CAFE", "LAUNDROMAT"].map(cat => (
                  <label key={cat} className="filter-chip">
                    <input
                      type="checkbox"
                      hidden
                      checked={filters.categories.includes(cat)}
                      onChange={() => handleCategoryChange(cat)}
                    />
                    <span className={`chip-text ${filters.categories.includes(cat) ? 'active' : ''}`}>
                      {cat.replace("_", " ")}
                    </span>
                  </label>
                ))}
              </div>

              <div className="price-filters">
                <select 
                  className="price-select"
                  value={filters.minPrice}
                  onChange={(e) => handlePriceChange("minPrice", e.target.value)}
                >
                  <option value="">Min Price</option>
                  {priceOptions.map(price => (
                    <option key={`min-${price}`} value={price}>₱{price}</option>
                  ))}
                </select>

                <select 
                  className="price-select"
                  value={filters.maxPrice}
                  onChange={(e) => handlePriceChange("maxPrice", e.target.value)}
                >
                  <option value="">Max Price</option>
                  {priceOptions.map(price => (
                    <option key={`max-${price}`} value={price}>₱{price}</option>
                  ))}
                </select>
              </div>
            </>
          )}

          <div className="sidebar-listings-scroll">

            {selectedListingId ? (
              <>
                <button 
                  onClick={() => setSelectedListingId(null)} 
                  style={{ marginBottom: "10px" }}
                >
                  ← Back
                </button>

                <ListingDetail id={selectedListingId} />
              </>
            ) : (
              <Listings 
                search={search}
                filters={filters}
                setParentListings={setListings}
                onSelectListing={setSelectedListingId}
              />
            )}

          </div>
          
          {!selectedListingId && (
            <button 
              className="clear-filters-link" 
              onClick={() => setFilters({ categories: [], minPrice: "", maxPrice: "" })}
            >
              Clear all filters
            </button>
          )}
        </aside>

        <section className="map-view">
          <MapView 
            listings={filteredListings}
            selectedListingId={selectedListingId}
          />
        </section>
      </main>
    </div>
  )
}

export default StudentDashboard;