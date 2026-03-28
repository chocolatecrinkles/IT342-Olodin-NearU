import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import StudentDashboard from "./pages/StudentDashboard";
import BusinessOwnerDashboard from "./pages/BusinessOwnerDashboard";
import AddListing from "./pages/AddListing"
import MyListings from "./pages/MyListings"

function App() {

  return (

    <BrowserRouter>

      <Routes>

        <Route path="/" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/student" element={<StudentDashboard />} />

        <Route path="/businessowner" element={<BusinessOwnerDashboard />} />
        <Route path="/businessowner/add" element={<AddListing />} />
        <Route path="/businessowner/list" element={<MyListings />} />
      </Routes>

    </BrowserRouter>

  );

}

export default App;