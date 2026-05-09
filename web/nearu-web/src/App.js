import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import StudentDashboard from "./features/student-dashboard/StudentDashboard";
import BusinessOwnerDashboard from "./pages/BusinessOwnerDashboard";
import AddListing from "./pages/AddListing"
import BusinessOwnerListingsPage from "./pages/BusinessOwnerListingsPage"
import Bookmarks from "./pages/Bookmarks"
import ListingDetail from "./pages/ListingDetail"
import BusinessOwnerListingDetailPage from "./pages/BusinessOwnerListingDetailPage"
import ListingDetailPage from "./pages/ListingDetailPage"
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import SelectRole from "./pages/SelectRole";

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
  iconUrl: markerIcon,
  iconRetinaUrl: markerIcon2x, 
  shadowUrl: markerShadow,
});

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

        <Route path="/select-role" element={<SelectRole />} />
      </Routes>

    </BrowserRouter>

  );

}

export default App;