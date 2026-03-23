import React, { useEffect } from 'react'
import {
  MapContainer,
  TileLayer,
  Polyline,
  Marker,
  Popup,
  useMap
} from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png')
})

const iconoNormal = new L.Icon({
  iconUrl: require('leaflet/dist/images/marker-icon.png'),
  iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
  shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
  iconSize: [20, 33],
  iconAnchor: [10, 33],
  popupAnchor: [0, -33]
})

const iconoBarata = new L.DivIcon({
  className: '',
  html: `<div style="
    background-color: #1a73e8;
    width: 28px;
    height: 28px;
    border-radius: 50% 50% 50% 0;
    transform: rotate(-45deg);
    border: 3px solid white;
    box-shadow: 0 2px 6px rgba(0,0,0,0.4);
  "></div>`,
  iconSize: [28, 28],
  iconAnchor: [14, 28],
  popupAnchor: [0, -30]
})

function AjustarVista({ puntosRuta }) {
  const map = useMap()
  useEffect(() => {
    if (puntosRuta && puntosRuta.length > 0) {
      const bounds = L.latLngBounds(puntosRuta.map((p) => [p[1], p[0]]))
      map.fitBounds(bounds, { padding: [40, 40] })
    }
  }, [puntosRuta, map])
  return null
}

function MapaRuta({
  puntosRuta,
  gasolineras,
  masBarata,
  onSeleccionarGasolinera
}) {
  const posicionInicial = [40.4168, -3.7038]

  const puntosLeaflet = puntosRuta ? puntosRuta.map((p) => [p[1], p[0]]) : []

  return (
    <MapContainer
      center={posicionInicial}
      zoom={7}
      style={{ height: '100%', width: '100%' }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
      />

      {puntosLeaflet.length > 0 && (
        <>
          <Polyline
            positions={puntosLeaflet}
            color='#1a73e8'
            weight={4}
            opacity={0.8}
          />
          <AjustarVista puntosRuta={puntosRuta} />
        </>
      )}

      {gasolineras &&
        gasolineras.map((gasolinera) => {
          const esMasBarata = masBarata && gasolinera.id === masBarata.id
          return (
            <Marker
              key={gasolinera.id}
              position={[gasolinera.latitud, gasolinera.longitud]}
              icon={esMasBarata ? iconoBarata : iconoNormal}
              eventHandlers={{
                click: () => onSeleccionarGasolinera(gasolinera)
              }}
            >
              <Popup>
                <div style={{ minWidth: '160px' }}>
                  <p
                    style={{
                      fontWeight: '600',
                      marginBottom: '4px',
                      fontSize: '13px'
                    }}
                  >
                    {gasolinera.nombre}
                  </p>
                  <p
                    style={{
                      fontSize: '12px',
                      color: '#5f6368',
                      marginBottom: '6px'
                    }}
                  >
                    {gasolinera.municipio}
                  </p>
                  <p style={{ fontSize: '13px' }}>
                    Gasolina 95:{' '}
                    <strong style={{ color: '#1a73e8' }}>
                      {gasolinera.precioGasolina95}€
                    </strong>
                  </p>
                  {gasolinera.precioDiesel && (
                    <p style={{ fontSize: '13px' }}>
                      Diésel: <strong>{gasolinera.precioDiesel}€</strong>
                    </p>
                  )}
                  {esMasBarata && (
                    <p
                      style={{
                        fontSize: '11px',
                        color: '#1a73e8',
                        fontWeight: '600',
                        marginTop: '6px'
                      }}
                    >
                      ★ Más barata de la ruta
                    </p>
                  )}
                </div>
              </Popup>
            </Marker>
          )
        })}
    </MapContainer>
  )
}

export default MapaRuta
