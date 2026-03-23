import React, { useState } from 'react'
import './PanelLateral.css'

function PanelLateral({
  onBuscar,
  cargando,
  error,
  masBarata,
  ultimaActualizacion,
  gasolineraSeleccionada
}) {
  const [origen, setOrigen] = useState('')
  const [destino, setDestino] = useState('')

  const handleSubmit = (e) => {
    e.preventDefault()
    if (origen.trim() && destino.trim()) {
      onBuscar(origen, destino)
    }
  }

  const formatearFecha = (fecha) => {
    if (!fecha) return '-'
    return new Date(fecha).toLocaleString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div className='panel-lateral'>
      <div className='panel-header'>
        <h1>Gasolineras en Ruta</h1>
        <p>Encuentra la gasolina más barata en tu trayecto</p>
      </div>

      <form onSubmit={handleSubmit} className='formulario'>
        <div className='campo'>
          <label>Origen</label>
          <input
            type='text'
            value={origen}
            onChange={(e) => setOrigen(e.target.value)}
            placeholder='Ej: Madrid'
            disabled={cargando}
          />
        </div>
        <div className='campo'>
          <label>Destino</label>
          <input
            type='text'
            value={destino}
            onChange={(e) => setDestino(e.target.value)}
            placeholder='Ej: Toledo'
            disabled={cargando}
          />
        </div>
        <button
          type='submit'
          disabled={cargando || !origen.trim() || !destino.trim()}
        >
          {cargando ? 'Buscando...' : 'Buscar ruta'}
        </button>
      </form>

      {error && <div className='error-mensaje'>{error}</div>}

      {masBarata && (
        <div className='mas-barata'>
          <h2>Gasolinera más barata</h2>
          <div className='gasolinera-card destacada'>
            <span className='etiqueta-barata'>Más barata</span>
            <p className='nombre'>{masBarata.nombre}</p>
            <p className='municipio'>
              {masBarata.municipio}, {masBarata.provincia}
            </p>
            <p className='precio'>
              Gasolina 95: <strong>{masBarata.precioGasolina95}€</strong>
            </p>
            {masBarata.precioDiesel && (
              <p className='precio'>
                Diésel: <strong>{masBarata.precioDiesel}€</strong>
              </p>
            )}
          </div>
        </div>
      )}

      {gasolineraSeleccionada && (
        <div className='gasolinera-detalle'>
          <h2>Detalle gasolinera</h2>
          <div className='gasolinera-card'>
            <p className='nombre'>{gasolineraSeleccionada.nombre}</p>
            <p className='detalle-linea'>
              <span>Marca:</span> {gasolineraSeleccionada.marca}
            </p>
            <p className='detalle-linea'>
              <span>Dirección:</span> {gasolineraSeleccionada.direccion}
            </p>
            <p className='detalle-linea'>
              <span>Municipio:</span> {gasolineraSeleccionada.municipio}
            </p>
            <p className='detalle-linea'>
              <span>Provincia:</span> {gasolineraSeleccionada.provincia}
            </p>
            <p className='precio'>
              Gasolina 95:{' '}
              <strong>{gasolineraSeleccionada.precioGasolina95}€</strong>
            </p>
            {gasolineraSeleccionada.precioDiesel && (
              <p className='precio'>
                Diésel: <strong>{gasolineraSeleccionada.precioDiesel}€</strong>
              </p>
            )}
          </div>
        </div>
      )}

      <div className='ultima-actualizacion'>
        <p>
          Última actualización:{' '}
          {formatearFecha(ultimaActualizacion?.fechaDescarga)}
        </p>
      </div>
    </div>
  )
}

export default PanelLateral
