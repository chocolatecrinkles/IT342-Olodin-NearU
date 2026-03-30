import { useState, useEffect } from "react"

const API = "http://localhost:8080/api/listings"

function Listings({ search }) {

    const [listings, setListings] = useState([])

    useEffect(() => {
        fetch(API)
            .then(res => res.json())
            .then(data => setListings(data))
    }, [])

    const filtered = listings.filter(l =>
        l.name.toLowerCase().includes(search.toLowerCase())
    )

    return (
        <div>
            {filtered.map(l => (
                <div key={l.id}>
                    <h3>{l.name}</h3>
                    <p>{l.address}</p>
                    <p>{l.price}</p>

                    <button>Bookmark</button>
                </div>
            ))}
        </div>
    )
}

export default Listings