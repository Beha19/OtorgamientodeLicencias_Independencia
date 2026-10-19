window.addEventListener('load', function () {
  const div = document.getElementById('mapa');
  if (!div) return;

  const lat = document.querySelector('input[name="latitud"]');
  const lng = document.querySelector('input[name="longitud"]');
  const info = document.getElementById('infoUbicacion');

  // Mapa centrado en Independencia
  const mapa = L.map('mapa').setView([-11.985, -77.06], 15);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
  }).addTo(mapa);

  // Dibuja los polígonos de las zonas
  fetch(div.dataset.geojson)
    .then(r => r.json())
    .then(datos => {
      const capa = L.geoJSON(datos, {
        style: { color: '#015BC1', weight: 2, fillOpacity: 0.12 },
        onEachFeature: (f, l) => {
          const p = f.properties || {};
          l.bindTooltip(p.nombre || p.name || p.codigo || 'Zona');
        }
      }).addTo(mapa);
      mapa.fitBounds(capa.getBounds());
    });

  // El pin: se crea con el primer clic y se puede arrastrar
  let pin = null;
  function fijar(la, lo) {
    lat.value = la.toFixed(6);
    lng.value = lo.toFixed(6);
    info.textContent = 'Ubicación marcada: ' + lat.value + ', ' + lng.value;
  }
  function ponerPin(la, lo) {
    if (pin) {
      pin.setLatLng([la, lo]);
    } else {
      pin = L.marker([la, lo], { draggable: true }).addTo(mapa);
      pin.on('dragend', e => {
        const p = e.target.getLatLng();
        fijar(p.lat, p.lng);
      });
    }
    fijar(la, lo);
  }
  mapa.on('click', e => ponerPin(e.latlng.lat, e.latlng.lng));

  // Si el formulario vuelve con error, recupera el pin que ya había puesto
  if (lat.value && lng.value) {
    ponerPin(parseFloat(lat.value), parseFloat(lng.value));
    mapa.setView([parseFloat(lat.value), parseFloat(lng.value)], 17);
  }
});