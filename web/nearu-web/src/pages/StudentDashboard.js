import { useState } from "react"
import Listings from "./Listings"
import { useNavigate } from "react-router-dom"
import "./css/StudentDashboard.css"
import ListingDetail from "./ListingDetail"

function StudentDashboard() {
  const navigate = useNavigate()
  const [search, setSearch] = useState("")
  const [selectedListingId, setSelectedListingId] = useState(null)
  const [filters, setFilters] = useState({
    categories: []
  })

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
                onSelectListing={setSelectedListingId}
              />
            )}

          </div>
          
          {!selectedListingId && (
            <button 
              className="clear-filters-link" 
              onClick={() => setFilters({ categories: [] })}
            >
              Clear all filters
            </button>
          )}
        </aside>

        <section className="map-view">
          
        </section>
      </main>
    </div>
  )
}

export default StudentDashboard;