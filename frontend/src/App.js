import React, { useState } from 'react'
import MapaRuta from './components/MapaRuta'
import PanelLateral from './components/PanelLateral'
import './App.css'

function App() {
  const [resultado, setResultado] = useState(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)
  const [gasolineraSeleccionada, setGasolineraSeleccionada] = useState(null)

  const buscarRuta = async (origen, destino) => {
    setCargando(true)
    setError(null)
    setGasolineraSeleccionada(null)
    try {
      const response = await fetch(
        `http://localhost:8080/api/ruta?origen=${encodeURIComponent(origen)}&destino=${encodeURIComponent(destino)}`
      )
      if (!response.ok) throw new Error('Error al obtener la ruta')
      const data = await response.json()
      setResultado(data)
    } catch (err) {
      setError(
        'No se pudo obtener la ruta. Comprueba los datos e inténtalo de nuevo.'
      )
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className='app-container'>
      <PanelLateral
        onBuscar={buscarRuta}
        cargando={cargando}
        error={error}
        masBarata={resultado?.masBarata}
        ultimaActualizacion={resultado?.ultimaActualizacion}
        gasolineraSeleccionada={gasolineraSeleccionada}
      />
      <div className='mapa-container'>
        <MapaRuta
          puntosRuta={resultado?.puntosRuta}
          gasolineras={resultado?.gasolineras}
          masBarata={resultado?.masBarata}
          onSeleccionarGasolinera={setGasolineraSeleccionada}
        />
      </div>
    </div>
  )
}

export default App
