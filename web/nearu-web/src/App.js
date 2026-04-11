import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import StudentDashboard from "./pages/StudentDashboard";
import BusinessOwnerDashboard from "./pages/BusinessOwnerDashboard";
import AddListing from "./pages/AddListing"
import BusinessOwnerListingsPage from "./pages/BusinessOwnerListingsPage"
import Bookmarks from "./pages/Bookmarks"
import ListingDetail from "./pages/ListingDetail"
import BusinessOwnerListingDetailPage from "./pages/BusinessOwnerListingDetailPage"
import ListingDetailPage from "./pages/ListingDetailPage"

function App() {

  return (

    <BrowserRouter>

      <Routes>

        <Route path="/" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/student" element={<StudentDashboard />} />

        <Route path="/businessowner" element={<BusinessOwnerDashboard />} />
        <Route path="/businessowner/add" element={<AddListing />} />
        <Route path="/businessowner/list" element={<BusinessOwnerListingsPage />} />
        <Route path="/businessowner/list/:id" element={<BusinessOwnerListingDetailPage />} />
        
        <Route path="/bookmarks" element={<Bookmarks/>} />
        <Route path="/listing/view/:id" element={<ListingDetailPage />} />

        <Route path="/listing/:id" element={<ListingDetail />} />
      </Routes>

    </BrowserRouter>

  );

}

export default App;