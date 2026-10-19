-- ########################################################################################
-- -----------------------BASE DE DATOS: licencias_db--------------------------------------
-- ########################################################################################
-- SISTEMA DE LICENCIA DE FUNCIONAMIENTO - MUNICIPALIDAD DE INDEPENDENCIA
-- Incluye:
--         1) tablas
--         2) catalogos y zonas SIG
--         3) expedientes de prueba
-- Clave de todos los usuarios: 123456
-- ########################################################################################

DROP DATABASE IF EXISTS licencias_db;
CREATE DATABASE IF NOT EXISTS licencias_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE licencias_db;

-- ========================================================================================
-- PARTE 1: TABLAS
-- ========================================================================================
-- ----------------------------------------------------------------------------------------
-- Usuarios y catalogos
-- ----------------------------------------------------------------------------------------
-- Actores del sistema, cada uno con su rol -----------------------------------------------
CREATE TABLE usuario (
  id_usuario        INT AUTO_INCREMENT PRIMARY KEY,
  dni               CHAR(8)      NOT NULL UNIQUE,
  nombres           VARCHAR(100) NOT NULL,
  apellidos         VARCHAR(100) NOT NULL,
  correo            VARCHAR(100) NOT NULL UNIQUE,
  password          VARCHAR(100) NOT NULL,                 -- hash BCrypt, nunca texto plano
  telefono          VARCHAR(15)  NULL,
  rol               VARCHAR(20)  NOT NULL,
  disponible        BOOLEAN      NOT NULL DEFAULT TRUE,    -- usado para tecnicos
  ultima_asignacion DATETIME     NULL,                     -- usado para tecnicos
  activo            BOOLEAN      NOT NULL DEFAULT TRUE,    -- eliminacion logica
  CONSTRAINT chk_usuario_rol
    CHECK (rol IN ('SOLICITANTE','REVISOR','TECNICO','SUBGERENTE','ADMIN'))
);

-- MANTENIMIENTO ---- Actividades economicas (Indice de Usos) -----------------------------
CREATE TABLE giro (
  id_giro       INT AUTO_INCREMENT PRIMARY KEY,
  codigo_ciiu   VARCHAR(10)  NOT NULL UNIQUE,
  nombre        VARCHAR(150) NOT NULL,
  nivel_riesgo  VARCHAR(10)  NOT NULL,
  activo        BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_giro_riesgo
    CHECK (nivel_riesgo IN ('BAJO','MEDIO','ALTO','MUY_ALTO'))
);

-- MANTENIMIENTO ---- Zonificacion del distrito ------------------------------------------
CREATE TABLE zona (
  id_zona      INT AUTO_INCREMENT PRIMARY KEY,
  codigo       VARCHAR(10)  NOT NULL UNIQUE,             -- RDM, CV, CZ...
  nombre       VARCHAR(100) NOT NULL,
  descripcion  VARCHAR(255) NULL,
  activo       BOOLEAN      NOT NULL DEFAULT TRUE,
  poligono     POLYGON SRID 4326 NULL                    -- forma de la zona en el mapa (SIG)
);

-- Giro permitido en que zona (si la fila existe, esta permitido) ------------------------
CREATE TABLE giro_zona (
  id_giro INT NOT NULL,
  id_zona INT NOT NULL,
  PRIMARY KEY (id_giro, id_zona),
  FOREIGN KEY (id_giro) REFERENCES giro(id_giro),
  FOREIGN KEY (id_zona) REFERENCES zona(id_zona)
);

-- MANTENIMIENTO ---- Para calcular los 15 dias habiles ----------------------------------
CREATE TABLE feriado (
  fecha        DATE         PRIMARY KEY,
  descripcion  VARCHAR(100) NOT NULL
);

-- RENIEC simulado: DNI de prueba --------------------------------------------------------
CREATE TABLE persona_reniec (
  dni               CHAR(8)      PRIMARY KEY,
  nombres           VARCHAR(100) NOT NULL,
  apellidos         VARCHAR(100) NOT NULL,
  fecha_nacimiento  DATE         NOT NULL
);


-- ---------------------------------------------------------------------------------------
-- Solicitud (Formulario 1) - SOLICITUD DE LICENCIA
-- ---------------------------------------------------------------------------------------
-- Local Fisico, con direccion estructurada ("EVITA DUPLICADOS") -------------------------
CREATE TABLE establecimiento (
  id_establecimiento INT AUTO_INCREMENT PRIMARY KEY,
  via_tipo    VARCHAR(10)   NOT NULL,                    -- AV, JR, CALLE, PSJE
  via_nombre  VARCHAR(100)  NOT NULL,
  numero      VARCHAR(10)   NOT NULL,
  interior    VARCHAR(20)   NOT NULL DEFAULT '',         -- '' y no NULL para que funcione el UNIQUE
  referencia  VARCHAR(255)  NULL,
  area_m2     DECIMAL(8,2)  NOT NULL,
  latitud     DECIMAL(10,7) NULL,
  longitud    DECIMAL(10,7) NULL,
  id_zona     INT           NOT NULL,
  FOREIGN KEY (id_zona) REFERENCES zona(id_zona),
  CONSTRAINT uk_establecimiento_direccion
    UNIQUE (via_tipo, via_nombre, numero, interior),
  CONSTRAINT chk_via_tipo
    CHECK (via_tipo IN ('AV','JR','CALLE','PSJE')),
  CONSTRAINT chk_area_maxima   -- El tramite es solo para locales de hasta 100 m2
    CHECK (area_m2 > 0 AND area_m2 <= 100)
);

-- Solicitud y todo su Recorrido. Centro del Sistema --------------------------------------
CREATE TABLE expediente (
  id_expediente          INT AUTO_INCREMENT PRIMARY KEY,
  numero                 VARCHAR(15)  NULL UNIQUE,                         -- 2026-000153, se asigna al enviar
  id_solicitante         INT          NOT NULL,
  id_establecimiento     INT          NOT NULL,
  id_giro                INT          NOT NULL,
  modalidad              VARCHAR(15)  NOT NULL DEFAULT 'INDETERMINADA',
  fecha_fin_vigencia     DATE         NULL,                                -- solo si es TEMPORAL
  tipo_persona           VARCHAR(10)  NOT NULL DEFAULT 'NATURAL',
  nombre_comercial       VARCHAR(150) NOT NULL,
  actividad              VARCHAR(255) NULL,
  autorizacion_sectorial VARCHAR(30)  NULL,
  ruc                    CHAR(11)     NULL,                                -- si es persona juridica
  razon_social           VARCHAR(150) NULL,
  estado                 VARCHAR(25)  NOT NULL DEFAULT 'BORRADOR',
  fecha_registro         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_envio            DATETIME     NULL,
  fecha_limite           DATE         NULL,                                -- 15 dias habiles, se calcula al enviar
  id_revisor             INT          NULL,
  id_tecnico_asignado    INT          NULL,
  fecha_asignacion       DATETIME     NULL,
  establecimiento_activo INT GENERATED ALWAYS AS (        -- Columna calculada: solo tiene valor si el expediente esta activo
    CASE WHEN estado IN ('BORRADOR','PAGADO_SIN_ENVIO','PENDIENTE_REVISION',
                         'VERIFICADO','OBSERVADO','EN_EVALUACION')
         THEN id_establecimiento END) STORED,
  FOREIGN KEY (id_solicitante)      REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_establecimiento)  REFERENCES establecimiento(id_establecimiento),
  FOREIGN KEY (id_giro)             REFERENCES giro(id_giro),
  FOREIGN KEY (id_revisor)          REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_tecnico_asignado) REFERENCES usuario(id_usuario),
  CONSTRAINT uk_un_expediente_activo UNIQUE (establecimiento_activo),  -- Un solo expediente activo por establecimiento
  CONSTRAINT chk_modalidad    CHECK (modalidad IN ('INDETERMINADA','TEMPORAL')),
  CONSTRAINT chk_tipo_persona CHECK (tipo_persona IN ('NATURAL','JURIDICA')),
  CONSTRAINT chk_expediente_estado CHECK (estado IN
    ('BORRADOR','PAGADO_SIN_ENVIO','PENDIENTE_REVISION','VERIFICADO','OBSERVADO',
     'EN_EVALUACION','APROBADO','DENEGADO','IMPROCEDENTE'))
);

-- Representante del Solicitante. Tabla Aparte: NO todos tienen Representante
CREATE TABLE representante (
  id_representante INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente    INT          NOT NULL UNIQUE,         -- un representante por expediente
  dni              CHAR(8)      NOT NULL,
  nombres          VARCHAR(100) NOT NULL,
  apellidos        VARCHAR(100) NOT NULL,
  tipo_poder       VARCHAR(25)  NOT NULL,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  CONSTRAINT chk_tipo_poder CHECK (tipo_poder IN ('CARTA_PODER_LEGALIZADA','VIGENCIA_PODER'))
);

-- Entidades Documentarias subidas(carta poder legalizada, planos, etc.) -----------------
CREATE TABLE documento_adjunto (
  id_documento    INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente   INT          NOT NULL,
  tipo            VARCHAR(30)  NOT NULL,
  nombre_archivo  VARCHAR(255) NOT NULL,
  ruta            VARCHAR(500) NOT NULL,                 -- el archivo va en una carpeta, aqui solo su ruta
  fecha_subida    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  CONSTRAINT chk_documento_tipo CHECK (tipo IN
    ('COPIA_DNI_CE','CARTA_PODER_LEGALIZADA','VIGENCIA_PODER','PLANO','OTRO'))
);
 
 
 -- --------------------------------------------------------------------------------------
-- Declaracion jurada (Formulario 2) - CONDICIONES DE SEGURIDAD
-- ---------------------------------------------------------------------------------------
CREATE TABLE condicion_seguridad (
  id_condicion  INT AUTO_INCREMENT PRIMARY KEY,
  grupo         VARCHAR(60)  NOT NULL,                   -- Condiciones basicas, Medios de evacuacion...
  descripcion   VARCHAR(255) NOT NULL,
  activo        BOOLEAN      NOT NULL DEFAULT TRUE
);
 
CREATE TABLE declaracion_jurada (
  id_declaracion   INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente    INT          NOT NULL UNIQUE,         -- una DJ por expediente
  aforo            INT          NULL,
  antiguedad_anios INT          NULL,
  area_techada_m2  DECIMAL(8,2) NULL,
  acepta_dj        BOOLEAN      NOT NULL DEFAULT FALSE,
  fecha            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente)
);
 
CREATE TABLE respuesta_condicion (
  id_declaracion INT     NOT NULL,
  id_condicion   INT     NOT NULL,
  cumple         BOOLEAN NOT NULL,                       -- TRUE = Si cumple, FALSE = No corresponde
  PRIMARY KEY (id_declaracion, id_condicion),
  FOREIGN KEY (id_declaracion) REFERENCES declaracion_jurada(id_declaracion),
  FOREIGN KEY (id_condicion)   REFERENCES condicion_seguridad(id_condicion)
);
 
 
 -- ---------------------------------------------------------------------------------------
-- PAGO, REVISION, EVALUACION Y EMISION
-- ----------------------------------------------------------------------------------------
-- Pago Derecho de Tramite (tarjeta, billetera o caja) con recibo -------------------------
CREATE TABLE pago (
  id_pago              INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente        INT           NOT NULL,
  codigo_orden         VARCHAR(20)   NULL UNIQUE,
  monto                DECIMAL(10,2) NOT NULL,
  medio                VARCHAR(15)   NOT NULL DEFAULT 'TARJETA',
  estado               VARCHAR(15)   NOT NULL DEFAULT 'PENDIENTE',
  referencia_pasarela  VARCHAR(100)  NULL,
  fecha                DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  numero_recibo        VARCHAR(20)   NULL UNIQUE,
  fecha_confirmacion   DATETIME      NULL,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  CONSTRAINT chk_pago_medio  CHECK (medio IN ('TARJETA','BILLETERA','CAJA')),
  CONSTRAINT chk_pago_estado CHECK (estado IN ('PENDIENTE','APROBADO','RECHAZADO'))
);
 
-- Observaciones del Revisor o del Tecnico (base del oficio en PDF) -----------------------
CREATE TABLE observacion (
  id_observacion  INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente   INT          NOT NULL,
  id_usuario      INT          NOT NULL,                 -- quien observa
  etapa           VARCHAR(15)  NOT NULL,
  numero_oficio   VARCHAR(20)  NOT NULL UNIQUE,
  descripcion     TEXT         NOT NULL,
  fecha           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subsanada       BOOLEAN      NOT NULL DEFAULT FALSE,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_usuario)    REFERENCES usuario(id_usuario),
  CONSTRAINT chk_observacion_etapa CHECK (etapa IN ('REVISION','EVALUACION'))
);
 
-- Tecnico que se le asigno cada expediente y por que (reparto equitativo)
CREATE TABLE historial_asignacion (
  id_historial     INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente    INT          NOT NULL,
  id_tecnico       INT          NOT NULL,
  id_asignado_por  INT          NOT NULL,
  tipo             VARCHAR(10)  NOT NULL,
  motivo           VARCHAR(255) NULL,                    -- obligatorio si es MANUAL
  fecha            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente)   REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_tecnico)      REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_asignado_por) REFERENCES usuario(id_usuario),
  CONSTRAINT chk_asignacion_tipo CHECK (tipo IN ('AUTOMATICA','MANUAL'))
);
 
-- Licencia Emitida (base del certificado en PDF)
CREATE TABLE licencia (
  id_licencia    INT AUTO_INCREMENT PRIMARY KEY,
  numero         VARCHAR(15) NOT NULL UNIQUE,
  id_expediente  INT         NOT NULL UNIQUE,            -- un expediente da una sola licencia
  id_firmante    INT         NOT NULL,                   -- el Sub Gerente
  fecha_emision  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado         VARCHAR(10) NOT NULL DEFAULT 'VIGENTE',
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_firmante)   REFERENCES usuario(id_usuario),
  CONSTRAINT chk_licencia_estado CHECK (estado IN ('VIGENTE','REVOCADA'))
);
 
 
 
-- ========================================================================================
-- PARTE 2: CATALOGOS Y ZONAS DEL SIG
-- ========================================================================================
-- Usuarios de prueba - todos con la clave 123456 (hash BCrypt)
--  1 admin@muni.pe ADMIN            5 subgerente@muni.pe SUBGERENTE
--  2 revisor@muni.pe REVISOR        6 juan.perez@correo.com SOLICITANTE
--  3 tecnico1@muni.pe TECNICO       7 maria.lopez@correo.com SOLICITANTE
--  4 tecnico2@muni.pe TECNICO
INSERT INTO usuario (dni, nombres, apellidos, correo, password, rol) VALUES
('10000001','Admin','Sistema','admin@muni.pe',         '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','ADMIN'),
('10000002','Rosa','Quispe','revisor@muni.pe',          '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','REVISOR'),
('10000003','Carlos','Ruiz','tecnico1@muni.pe',         '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','TECNICO'),
('10000004','Lucia','Torres','tecnico2@muni.pe',        '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','TECNICO'),
('10000005','Jorge','Mendoza','subgerente@muni.pe',     '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','SUBGERENTE'),
('10000006','Juan','Perez','juan.perez@correo.com',     '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','SOLICITANTE'),
('20000001','Maria','Lopez','maria.lopez@correo.com',   '$2a$10$3XJU4U/vAiF1lg0hUbSTHeocBOch0Nik4XvqpDSquei4VFj/ERjXO','SOLICITANTE');
 
-- Zonas: 1 RDM, 2 CV, 3 CZ, 4 I1, 5 ZRP, 6 E1  -------------------------------------------
INSERT INTO zona (codigo, nombre, descripcion) VALUES
('RDM','Residencial de Densidad Media','Vivienda con comercio local permitido'),
('CV', 'Comercio Vecinal',             'Comercio diario de pequena escala'),
('CZ', 'Comercio Zonal',               'Comercio de mayor escala'),
('I1', 'Industria Elemental',          'Talleres y actividades complementarias'),
('ZRP','Zona de Recreacion Publica',   'Parques y areas verdes'),
('E1', 'Educacion Basica',             'Colegios');
 
-- Poligonos de las zonas dibujadas en geojson.io (solo la parte "geometry")  --------------
UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0605601,-11.9842799],[-77.0604857,-11.9847402],[-77.0590323,-11.9845487],
  [-77.0591334,-11.9840759],[-77.0593063,-11.9841055],[-77.0605601,-11.9842799]]]}')
WHERE codigo = 'RDM';
 
UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.062704,-11.985579],[-77.0626294,-11.9860009],[-77.060843,-11.9857218],
  [-77.0609134,-11.9852995],[-77.0618085,-11.9854426],[-77.062704,-11.985579]]]}')
WHERE codigo = 'CZ';
 
UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0627104,-11.9855344],[-77.0612529,-11.9852987],[-77.0613179,-11.9848653],
  [-77.060997,-11.9848316],[-77.0611152,-11.9841215],[-77.0628743,-11.9843945],
  [-77.0627924,-11.9849288],[-77.0627104,-11.9855344]]]}')
WHERE codigo = 'ZRP';
 
UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0626198,-11.9838406],[-77.0612001,-11.9836128],[-77.0611162,-11.9841215],
  [-77.0625405,-11.9843419],[-77.0626198,-11.9838406]]]}')
WHERE codigo = 'E1';
 
-- Giros: 1 Bodega, 2 Restaurante, 3 Peluqueria, 4 Taller mecanico  -------------------------
INSERT INTO giro (codigo_ciiu, nombre, nivel_riesgo) VALUES
('4711','Bodega',          'BAJO'),
('5610','Restaurante',     'MEDIO'),
('9602','Peluqueria',      'BAJO'),
('4520','Taller mecanico', 'ALTO');
 
-- Indice de Usos: bodega en RDM, CV, CZ; restaurante en CV, CZ; peluqueria en RDM, CV, CZ; taller en CZ, I1
INSERT INTO giro_zona (id_giro, id_zona) VALUES
(1,1),(1,2),(1,3),
(2,2),(2,3),
(3,1),(3,2),(3,3),
(4,3),(4,4);
 
INSERT INTO feriado (fecha, descripcion) VALUES
('2026-10-08','Combate de Angamos'),
('2026-11-01','Todos los Santos'),
('2026-12-08','Inmaculada Concepcion'),
('2026-12-09','Batalla de Ayacucho'),
('2026-12-25','Navidad');
 
INSERT INTO persona_reniec (dni, nombres, apellidos, fecha_nacimiento) VALUES
('10000006','Juan','Perez Gomez','1990-05-14'),
('20000001','Maria','Lopez Diaz','1985-11-02'),
('20000002','Pedro','Salas Rios','1978-03-27');
 
INSERT INTO condicion_seguridad (grupo, descripcion) VALUES
('Condiciones basicas','El establecimiento no se encuentra en proceso de construccion'),
('Condiciones basicas','Cuenta con servicios de agua, electricidad y servicios esenciales instalados'),
('Condiciones basicas','Cuenta con mobiliario basico e instalado para la actividad'),
('Condiciones basicas','Los equipos o artefactos estan instalados en sus lugares de uso habitual'),
('Medios de evacuacion','Los medios de evacuacion tienen ancho adecuado'),
('Medios de evacuacion','Pasadizos, escaleras, accesos y salidas libres de obstaculos'),
('Medios de evacuacion','Cuenta con senalizacion de seguridad y luces de emergencia operativas'),
('Medios de evacuacion','Las puertas de evacuacion abren en el sentido del flujo');
 
 
 
-- ==========================================================================================
-- PARTE 3: EXPEDIENTES DE PRUEBA (uno en cada estado del flujo)
-- ==========================================================================================
-- Un local por expediente: la regla "un expediente activo por local", NO deja REPETIR local en EXPEDIENTES ACTIVOS
INSERT INTO establecimiento (via_tipo, via_nombre, numero, interior, referencia, area_m2, id_zona) VALUES
('AV',    'Tupac Amaru',     '1250', '',  'Frente al mercado', 45.50, 3),  -- 1
('AV',    'Los Alisos',      '310',  '',  NULL,                80.00, 3),  -- 2
('JR',    'Las Anonas',      '145',  '',  'Cerca al parque',   30.00, 1),  -- 3
('CALLE', 'Las Tunas',       '220',  'A', NULL,                60.00, 2),  -- 4
('AV',    'Las Almendras',   '505',  '',  NULL,                95.00, 3),  -- 5
('PSJE',  'Los Membrillos',  '12',   '',  NULL,                25.00, 1),  -- 6
('AV',    'Carlos Izaguirre','890',  '2', 'Segundo piso',      70.00, 2);  -- 7
 
-- fecha_limite variada para probar el semaforo de la bandeja  -------------------------------
INSERT INTO expediente
	 (numero, id_solicitante, id_establecimiento, id_giro, modalidad, tipo_persona,
	  nombre_comercial, actividad, estado, fecha_registro, fecha_envio, fecha_limite,
	  id_revisor, id_tecnico_asignado, fecha_asignacion)
VALUES
	-- 1: borrador (aun sin numero ni plazo)  ------------------------------------------------
	(NULL,          6, 1, 2, 'INDETERMINADA', 'NATURAL', 'Sabor Norteno',
	 'Venta de comida', 'BORRADOR', '2026-10-15 10:00:00', NULL, NULL, NULL, NULL, NULL),
	-- 2: pendiente de revision, plazo holgado (verde)
	('2026-000101', 6, 2, 1, 'INDETERMINADA', 'NATURAL', 'Bodega Don Juan',
	 'Venta de abarrotes', 'PENDIENTE_REVISION', '2026-10-14 09:00:00', '2026-10-14 09:30:00', '2026-11-04',
	 NULL, NULL, NULL),
	-- 3: pendiente de revision, plazo cercano (amarillo)
	('2026-000098', 7, 3, 3, 'INDETERMINADA', 'NATURAL', 'Peluqueria Maria',
	 'Corte y peinado', 'PENDIENTE_REVISION', '2026-10-05 11:00:00', '2026-10-05 11:20:00', '2026-10-26',
	 NULL, NULL, NULL),
	-- 4: pendiente de revision, plazo por vencer (rojo)
	('2026-000090', 7, 4, 1, 'INDETERMINADA', 'NATURAL', 'Minimarket Lopez',
	 'Venta de abarrotes', 'PENDIENTE_REVISION', '2026-09-29 08:00:00', '2026-09-29 08:15:00', '2026-10-20',
	 NULL, NULL, NULL),
	-- 5: observado por el revisor
	('2026-000095', 6, 5, 2, 'INDETERMINADA', 'NATURAL', 'Cevicheria El Puerto',
	 'Venta de comida', 'OBSERVADO', '2026-10-01 10:00:00', '2026-10-01 10:10:00', '2026-10-22',
	 2, NULL, NULL),
	-- 6: en evaluacion, asignado al tecnico 3
	('2026-000093', 7, 6, 3, 'INDETERMINADA', 'NATURAL', 'Estilos Pasaje',
	 'Corte y peinado', 'EN_EVALUACION', '2026-09-30 15:00:00', '2026-09-30 15:05:00', '2026-10-21',
	 2, 3, '2026-10-06 09:00:00'),
	-- 7: aprobado y con licencia firmada (para probar el certificado)
	('2026-000080', 6, 7, 1, 'INDETERMINADA', 'NATURAL', 'Bodega Izaguirre',
	 'Venta de abarrotes', 'APROBADO', '2026-09-15 09:00:00', '2026-09-15 09:10:00', '2026-10-06',
	 2, 4, '2026-09-18 10:00:00');
 
-- Representante del expediente 3 (tramite hecho por apoderado)  -----------------------------
INSERT INTO representante (id_expediente, dni, nombres, apellidos, tipo_poder) VALUES
(3, '20000002', 'Pedro', 'Salas Rios', 'CARTA_PODER_LEGALIZADA');
 
-- Documento adjunto del expediente 3  -------------------------------------------------------
INSERT INTO documento_adjunto (id_expediente, tipo, nombre_archivo, ruta, fecha_subida) VALUES
(3, 'CARTA_PODER_LEGALIZADA', 'carta_poder.pdf', 'uploads/2026-000098/carta_poder.pdf', '2026-10-05 11:10:00');
 
-- Declaracion jurada del expediente 2 con sus respuestas -------------------------------------
INSERT INTO declaracion_jurada (id_expediente, aforo, antiguedad_anios, area_techada_m2, acepta_dj, fecha) VALUES
(2, 15, 10, 80.00, TRUE, '2026-10-14 09:20:00');
 
INSERT INTO respuesta_condicion (id_declaracion, id_condicion, cumple) VALUES
(1,1,TRUE),(1,2,TRUE),(1,3,TRUE),(1,4,TRUE),(1,5,TRUE),(1,6,TRUE),(1,7,TRUE),(1,8,FALSE);
 
-- Pagos confirmados  -------------------------------------------------------------------------
INSERT INTO pago (id_expediente, codigo_orden, monto, medio, estado, numero_recibo, fecha, fecha_confirmacion) VALUES
(2, 'ORD-2026-0101', 120.50, 'TARJETA',   'APROBADO', 'REC-2026-0101', '2026-10-14 09:25:00', '2026-10-14 09:25:00'),
(3, 'ORD-2026-0098', 120.50, 'BILLETERA', 'APROBADO', 'REC-2026-0098', '2026-10-05 11:15:00', '2026-10-05 11:15:00'),
(7, 'ORD-2026-0080', 120.50, 'CAJA',      'APROBADO', 'REC-2026-0080', '2026-09-15 09:05:00', '2026-09-15 09:05:00');
 
-- Observacion del expediente 5 (base del oficio en PDF)  --------------------------------------
INSERT INTO observacion (id_expediente, id_usuario, etapa, numero_oficio, descripcion, fecha) VALUES
(5, 2, 'REVISION', 'OF-2026-0001',
 'La copia del DNI adjunta no es legible. Adjuntar nuevamente.', '2026-10-03 16:00:00');
 
-- Asignacion de los expedientes 6 y 7 a los tecnicos  -----------------------------------------
INSERT INTO historial_asignacion (id_expediente, id_tecnico, id_asignado_por, tipo, motivo, fecha) VALUES
(6, 3, 2, 'AUTOMATICA', NULL, '2026-10-06 09:00:00'),
(7, 4, 2, 'AUTOMATICA', NULL, '2026-09-18 10:00:00');
 
-- Licencia del expediente 7, firmada por el Sub Gerente (id 5)  -------------------------------
INSERT INTO licencia (numero, id_expediente, id_firmante, fecha_emision, estado) VALUES
('LIC-2026-0001', 7, 5, '2026-10-02 12:00:00', 'VIGENTE');
 
 
 
-- ==============================================================================================
-- Consulta del SIG (referencia para Java): en que zona cae un punto.
-- Punto dentro del parque: devuelve ZRP
-- SELECT codigo, nombre FROM zona
-- WHERE ST_Contains(poligono,
--       ST_GeomFromText('POINT(-77.0620 -11.9848)', 4326, 'axis-order=long-lat'));
-- ===============================================================================================