import React, { useState, useEffect } from 'react'
import MapaRuta from './components/MapaRuta'
import PanelLateral from './components/PanelLateral'
import './App.css'

function App() {
  const [resultado, setResultado] = useState(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)
  const [gasolineraSeleccionada, setGasolineraSeleccionada] = useState(null)
  const [marcas, setMarcas] = useState([])
  const [marcaSeleccionada, setMarcaSeleccionada] = useState('')

  const buscarRuta = async (origen, destino, marca) => {
    setCargando(true)
    setError(null)
    setGasolineraSeleccionada(null)
    try {
      const url = `http://localhost:8080/api/ruta?origen=${encodeURIComponent(origen)}&destino=${encodeURIComponent(destino)}${marca ? `&marca=${encodeURIComponent(marca)}` : ''}`
      const response = await fetch(url)
      if (!response.ok) throw new Error('Error al obtener la ruta')
      const data = await response.json()
      setResultado(data)
      if (data.totalEncontradas === 0) {
        setError(
          'No se encontraron gasolineras cerca de esta ruta. Prueba con otra.'
        )
      }
    } catch (err) {
      setError(
        'No se pudo obtener la ruta. Comprueba los datos e inténtalo de nuevo.'
      )
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => {
    fetch('http://localhost:8080/api/marcas')
      .then((res) => res.json())
      .then((data) => setMarcas(data))
      .catch(() => {})
  }, [])

  return (
    <div className='app-container'>
      <PanelLateral
        onBuscar={buscarRuta}
        cargando={cargando}
        error={error}
        masBarata={resultado?.masBarata}
        ultimaActualizacion={resultado?.ultimaActualizacion}
        gasolineraSeleccionada={gasolineraSeleccionada}
        marcas={marcas}
        marcaSeleccionada={marcaSeleccionada}
        onMarcaChange={setMarcaSeleccionada}
      />
      <div className='mapa-container'>
        <MapaRuta
          puntosRuta={resultado?.puntosRuta}
          gasolineras={resultado?.gasolineras}
          masBarata={resultado?.masBarata}
          precioMin={resultado?.precioMin}
          precioMax={resultado?.precioMax}
          onSeleccionarGasolinera={setGasolineraSeleccionada}
        />
      </div>
    </div>
  )
}

export default App
