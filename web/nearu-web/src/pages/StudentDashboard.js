import { useState } from "react"
import Listings from "./Listings"

function StudentDashboard() {

    const [search, setSearch] = useState("")

    return (
        <div>
            <h2>Explore NearU</h2>

            <input
                type="text"
                placeholder="Search listings..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
            />

            <Listings search={search} />
        </div>
    )
}

export default StudentDashboard