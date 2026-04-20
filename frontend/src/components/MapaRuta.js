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

function crearIconoColor(color, tamaño = 24) {
  return new L.DivIcon({
    className: '',
    html: `<div style="
      background-color: ${color};
      width: ${tamaño}px;
      height: ${tamaño}px;
      border-radius: 50% 50% 50% 0;
      transform: rotate(-45deg);
      border: 2px solid white;
      box-shadow: 0 2px 5px rgba(0,0,0,0.3);
    "></div>`,
    iconSize: [tamaño, tamaño],
    iconAnchor: [tamaño / 2, tamaño],
    popupAnchor: [0, -(tamaño + 4)]
  })
}

const iconoAzulFuerte = crearIconoColor('#2D5BE3', 32)
const iconoVerde = crearIconoColor('#43a047', 24)
const iconoAmarillo = crearIconoColor('#f9a825', 24)
const iconoRojo = crearIconoColor('#e53935', 24)

function obtenerIcono(gasolinera, masBarata, precioMin, precioMax) {
  if (masBarata && gasolinera.id === masBarata.id) return iconoAzulFuerte
  if (!gasolinera.precioGasolina95 || precioMin === precioMax)
    return iconoAmarillo

  const rango = precioMax - precioMin
  const tercio = rango / 3
  const precio = gasolinera.precioGasolina95

  if (precio <= precioMin + tercio) return iconoVerde
  if (precio <= precioMin + 2 * tercio) return iconoAmarillo
  return iconoRojo
}

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
  precioMin,
  precioMax,
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
        gasolineras.map((gasolinera) => (
          <Marker
            key={gasolinera.id}
            position={[gasolinera.latitud, gasolinera.longitud]}
            icon={obtenerIcono(gasolinera, masBarata, precioMin, precioMax)}
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
                {masBarata && gasolinera.id === masBarata.id && (
                  <p
                    style={{
                      fontSize: '11px',
                      color: '#1b5e20',
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
        ))}
    </MapContainer>
  )
}

export default MapaRuta
