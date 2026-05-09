import { MapContainer, TileLayer, Marker, useMapEvents } from "react-leaflet";
import { useState, useEffect } from "react";
import { useMap } from "react-leaflet";

function LocationMarker({ position, setPosition }) {
  const map = useMap();

  useMapEvents({
    click(e) {
      const { lat, lng } = e.latlng;
      const newPos = [lat, lng];

      setPosition(newPos);
      map.setView(newPos, map.getZoom()); // 🔥 center map
    },
  });

  return position ? <Marker position={position} /> : null;  
}

function MapSelector({ onSelect, initialPosition }) {
  const [position, setPosition] = useState(initialPosition || null);

  useEffect(() => {
    if (initialPosition && initialPosition.length === 2) {
      setPosition(initialPosition);
    }
  }, [initialPosition]);

  const handleSetPosition = (pos) => {
    setPosition(pos);
    onSelect(pos);
  };

  return (
    <MapContainer
      center={position || [10.3157, 123.8854]}
      zoom={15}
      style={{ height: "250px", width: "100%", borderRadius: "8px" }}
    >
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

      <LocationMarker position={position} setPosition={handleSetPosition} />
    </MapContainer>
  );
}

export default MapSelector;