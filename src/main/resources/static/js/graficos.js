/* =====================================================================
   graficos.js 
   ===================================================================== */
const COLORES = {
  azul: '#015BC1', oscuro: '#00367A', celeste: '#3CA6F2',
  amarillo: '#F1BA2B', verde: '#209C54', rojo: '#DA3431'
};

/** Barras: graficoBarras('g1', ['Lun','Mar'], [1, 2], COLORES.azul) */
function graficoBarras(idCanvas, etiquetas, datos, color) {
  const canvas = document.getElementById(idCanvas);
  if (!canvas || typeof Chart === 'undefined') return;
  new Chart(canvas, {
    type: 'bar',
    data: { labels: etiquetas, datasets: [{ data: datos, backgroundColor: color, borderRadius: 6, maxBarThickness: 34 }] },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, ticks: { precision: 0 } }, x: { grid: { display: false } } }
    }
  });
}

/** Dona: graficoDona('g2', ['Aprobados','Denegados'], [3, 1]) */
function graficoDona(idCanvas, etiquetas, datos) {
  const canvas = document.getElementById(idCanvas);
  if (!canvas || typeof Chart === 'undefined') return;
  new Chart(canvas, {
    type: 'doughnut',
    data: { labels: etiquetas, datasets: [{ data: datos,
      backgroundColor: [COLORES.azul, COLORES.amarillo, COLORES.verde, COLORES.rojo], borderWidth: 0 }] },
    options: { maintainAspectRatio: false, cutout: '65%',
               plugins: { legend: { position: 'right', labels: { boxWidth: 12 } } } }
  });
}