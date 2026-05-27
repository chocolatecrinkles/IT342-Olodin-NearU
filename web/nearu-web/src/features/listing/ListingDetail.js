import { useEffect, useState } from "react"

function ListingDetail({ id }) {

    const [listing, setListing] = useState(null)
    const [images, setImages] = useState([])
    const [currentIndex, setCurrentIndex] = useState(0)

    useEffect(() => {
        if (!id) return

        fetch(`http://localhost:8080/api/listings`)
            .then(res => res.json())
            .then(data => {
                const found = data.find(l => l.id == id)
                setListing(found)
            })

        fetch(`http://localhost:8080/api/listings/${id}/images`)
            .then(res => res.json())
            .then(data => {
                setImages(data)
                setCurrentIndex(0) 
            })

    }, [id])

    const nextImage = () => {
        setCurrentIndex(prev =>
            prev === images.length - 1 ? 0 : prev + 1
        )
    }

    const prevImage = () => {
        setCurrentIndex(prev =>
            prev === 0 ? images.length - 1 : prev - 1
        )
    }

    if (!listing) return <p style={{ padding: "20px" }}>Loading...</p>

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

    return (
        <div style={{ padding: "20px" }}>
            <h2>{listing.name}</h2>

            {images.length > 0 && (
                <div>
                    <button onClick={prevImage}>◀</button>

                    <img
                        src={`http://localhost:8080${images[currentIndex].imageUrl}`}
                        width="300"
                    />

                    <button onClick={nextImage}>▶</button>
                </div>
            )}

            <p><b>Category:</b> {listing.category}</p>
            <p><b>Address:</b> {listing.address}</p>
            <p><b>Price:</b>{displayPrice(listing)}</p>
            <p><b>Description:</b> {listing.description}</p>
        </div>
    )
}

export default ListingDetail;