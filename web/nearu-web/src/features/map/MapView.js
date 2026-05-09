import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { memo, useEffect } from "react";
import { useMap } from "react-leaflet"


function RecenterMap({ selectedListing }) {
  const map = useMap();

  useEffect(() => {
    if (
        selectedListing &&
        selectedListing.latitude != null &&
        selectedListing.longitude != null
    ) {
      map.setView([selectedListing.latitude, selectedListing.longitude], 16);
    } else {
        map.setView([10.3157, 123.8854], 13);
    }
  }, [selectedListing, map]);

  return null;
}

function MapView({ listings = [], selectedListingId }) {

  const displayListings = selectedListingId
    ? listings.filter(l => l.id === selectedListingId)
    : listings;

  return (
    <MapContainer
      center={[10.7202, 122.5621]}
      zoom={13}
      style={{ height: "100%", width: "100%" }}
    >
        <RecenterMap 
            selectedListing={
            selectedListingId
            ? listings.find(l => l.id === selectedListingId)
            : null
        }
        />

      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

      {displayListings.map(l =>
        l.latitude != null && l.longitude != null ? (
          <Marker key={l.id} position={[l.latitude, l.longitude]}>
            <Popup>
              <b>{l.name}</b><br />
              {l.address}
            </Popup>
          </Marker>
        ) : null
      )}
    </MapContainer>
  );
}

export default memo(MapView);