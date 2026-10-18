/* =====================================================================
layout.js
===================================================================== */
   document.addEventListener('DOMContentLoaded', function ()
   {
     const ahora = new Date();
     const h = ahora.getHours();                 // hora de 0 a 23

     // Momento del día según la hora del equipo
     let momento, icono;
     if (h < 12)      { momento = 'Buenos días';   icono = 'bi-brightness-high'; }   // 0:00 a 11:59
     else if (h < 19) { momento = 'Buenas tardes'; icono = 'bi-cloud-sun'; }         // 12:00 a 18:59
     else             { momento = 'Buenas noches'; icono = 'bi-moon-stars'; }        // 19:00 a 23:59

     const saludo = document.getElementById('saludo');
     if (saludo) saludo.textContent = '¡' + momento + ', ' + saludo.dataset.nombre + '!';

     const clima = document.getElementById('clima');
     if (clima) clima.classList.add(icono);

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
/* ================= CHAT DE AYUDA (respuestas básicas, sin IA) ================= */
(function () {
  const btn = document.getElementById('chatBtn');
  const ventana = document.getElementById('chatVentana');
  if (!btn || !ventana) return;               // si la página no tiene chat, no hace nada

  const msgs = document.getElementById('chatMsgs');
  const sug = document.getElementById('chatSug');
  const form = document.getElementById('chatForm');
  const input = document.getElementById('chatInput');

  // Cada respuesta: palabras clave que la activan + el texto con el que contesta
  const RESPUESTAS = [
    { claves: ['hola', 'buenas', 'buenos dias'],
      texto: '¡Hola! Soy el asistente de LicenControl. Puedo ayudarte con tu licencia de funcionamiento.' },
    { claves: ['inici', 'nuevo tramite', 'solicitar', 'como empiezo', 'registrar'],
      texto: 'Entra a «Nuevo trámite», completa los datos del local y del giro, adjunta tus documentos y envía. Te daremos un número de expediente.' },
    { claves: ['plazo', 'demora', 'cuanto tarda', 'cuanto demora', 'dias'],
      texto: 'El plazo máximo es de 15 días hábiles (no cuentan sábados, domingos ni feriados). En tu inicio ves la fecha límite de tu trámite.' },
    { claves: ['documento', 'requisito', 'necesito'],
      texto: 'Necesitas el DNI del titular, los datos del local y del giro y, según el caso, carta poder y planos. La lista final aparece al registrar el trámite.' },
    { claves: ['pago', 'pagar', 'costo', 'precio', 'recibo'],
      texto: 'El derecho de trámite se paga en línea con tarjeta o en caja. Tu recibo aparece en «Mis pagos».' },
    { claves: ['observado', 'observacion', 'subsanar', 'corregir'],
      texto: 'Si tu expediente sale «Observado», revisa el oficio en Notificaciones, corrige lo indicado y vuelve a enviarlo.' },
    { claves: ['estado', 'seguimiento', 'en que va', 'mi expediente'],
      texto: 'En «Mis expedientes» ves la etapa de cada trámite: Enviado, Revisión, Evaluación, Firma y Licencia.' },
    { claves: ['que es', 'licencia de funcionamiento'],
      texto: 'Es la autorización que da la Municipalidad para que un negocio funcione en un local. Este sistema atiende locales de hasta 100 m².' },
    { claves: ['horario', 'atencion', 'telefono', 'contacto'],
      texto: 'Atención al ciudadano: de lunes a viernes, de 8 a. m. a 5 p. m.' },
    { claves: ['gracias', 'chau', 'adios'],
      texto: '¡Con gusto! Si necesitas algo más, aquí estoy.' }
  ];
  const NO_ENTIENDO = 'No estoy seguro de haber entendido. Prueba con una palabra como «plazo», «documentos» o «pago», o elige una de las sugerencias.';
  const SUGERENCIAS = ['¿Cómo inicio un trámite?', '¿Cuánto demora?', '¿Qué documentos necesito?', '¿Cómo pago?'];

  // Quita tildes y mayúsculas: "Cuánto" y "cuanto" pasan a ser lo mismo
  function normalizar(t) {
    return t.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
  }

  // Busca la primera respuesta cuya palabra clave aparezca en el texto
  function responder(texto) {
    const n = normalizar(texto);
    const hallada = RESPUESTAS.find(r => r.claves.some(c => n.includes(normalizar(c))));
    return hallada ? hallada.texto : NO_ENTIENDO;
  }

  function agregar(texto, quien) {              // quien = 'bot' o 'yo'
    const d = document.createElement('div');
    d.className = 'm-' + quien;
    d.textContent = texto;                      // textContent, no innerHTML (seguridad)
    msgs.appendChild(d);
    msgs.scrollTop = msgs.scrollHeight;         // baja hasta el último mensaje
  }

  function enviar(texto) {
    texto = texto.trim();
    if (!texto) return;
    agregar(texto, 'yo');
    setTimeout(() => agregar(responder(texto), 'bot'), 400);
  }

  // Botoncitos de preguntas sugeridas
  SUGERENCIAS.forEach(s => {
    const b = document.createElement('button');
    b.type = 'button';
    b.textContent = s;
    b.addEventListener('click', () => enviar(s));
    sug.appendChild(b);
  });

  // Abrir y cerrar la ventana
  btn.addEventListener('click', () => {
    ventana.classList.toggle('abierta');
    if (ventana.classList.contains('abierta') && msgs.children.length === 0) {
      agregar('¡Hola! Soy el asistente de LicenControl. ¿En qué te ayudo?', 'bot');
    }
  });
  document.getElementById('chatCerrar').addEventListener('click', () => ventana.classList.remove('abierta'));

  // Enviar con Enter o con el botón
  form.addEventListener('submit', e => {
    e.preventDefault();                          // evita que la página se recargue
    enviar(input.value);
    input.value = '';
  });
})();


/* ================= MODO CLARO / OSCURO (interruptor) ================= */
(function () {
  const btn = document.getElementById('temaBtn');
  if (!btn) return;
  const raiz = document.documentElement;          // la etiqueta <html>

  function sincronizar() {
    btn.setAttribute('aria-checked', raiz.getAttribute('data-bs-theme') === 'dark');
  }
  sincronizar();

  btn.addEventListener('click', () => {
    const nuevo = raiz.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    raiz.setAttribute('data-bs-theme', nuevo);
    try { localStorage.setItem('tema', nuevo); } catch (e) {}
    sincronizar();
    if (typeof aplicarTemaGraficos === 'function') aplicarTemaGraficos();   // redibuja los gráficos con los colores del tema
  });
})();