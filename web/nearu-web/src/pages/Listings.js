import { useState, useEffect } from "react"

const API = "http://localhost:8080/api/listings"

function Listings() {

    const [listings, setListings] = useState([]);

    useEffect(() => {
        fetch(API)
            .then(res => res.json())
            .then(data => setListings(data))
    }, [])

    return (
        <div>
            <h2>Listings</h2>

            {listings.map(l => (
                <div key={l.id}>
                    <h3>{l.name}</h3>
                    <p>{l.address}</p>
                    <p>Price: {l.price}</p>
                </div>
            ))}
        </div>
    )
}

export default Listings;