/* =====================================================================
   layout.js
   ===================================================================== */
document.addEventListener('DOMContentLoaded', function () {
  const ahora = new Date();
  const h = ahora.getHours();

  const saludo = document.getElementById('saludo');
  if (saludo) {
    const momento = h < 12 ? 'Buenos días' : h < 19 ? 'Buenas tardes' : 'Buenas noches';
    saludo.textContent = '¡' + momento + ', ' + saludo.dataset.nombre + '!';
  }

  const clima = document.getElementById('clima');
  if (clima) clima.classList.add(h < 19 ? 'bi-brightness-high' : 'bi-cloud-moon');

  const fecha = document.getElementById('fecha');
  if (fecha) fecha.textContent = ahora.toLocaleDateString('es-PE',
      { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });

  const hora = document.getElementById('hora');
  if (hora) {
    const pintarHora = function () {
      hora.textContent = new Date().toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
    };
    pintarHora();
    setInterval(pintarHora, 30000);   // se actualiza cada 30 segundos
  }
});