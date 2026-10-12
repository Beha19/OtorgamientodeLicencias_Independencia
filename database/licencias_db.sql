-- =====================================================================
-- Sistema de Licencias de Funcionamiento - Base de datos (MySQL 8)
-- Parte 1: NUCLEO (necesario)
-- Parte 2: EXTRAS (si el tiempo alcanza)
-- Parte 3: DATOS DE PRUEBA
-- =====================================================================

CREATE DATABASE IF NOT EXISTS licencias_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE licencias_db;

-- =====================================================================
-- PARTE 1: NUCLEO
-- =====================================================================

-- Todos los que entran al sistema, cada uno con su rol
CREATE TABLE usuario (
  id_usuario        INT AUTO_INCREMENT PRIMARY KEY,
  dni               CHAR(8)      NOT NULL UNIQUE,
  nombres           VARCHAR(100) NOT NULL,
  apellidos         VARCHAR(100) NOT NULL,
  correo            VARCHAR(100) NOT NULL UNIQUE,
  password          VARCHAR(100) NOT NULL,          -- hash BCrypt, nunca texto plano
  telefono          VARCHAR(15)  NULL,
  rol               VARCHAR(20)  NOT NULL,
  disponible        BOOLEAN      NOT NULL DEFAULT TRUE,   -- usado para tecnicos
  ultima_asignacion DATETIME     NULL,                    -- usado para tecnicos
  activo            BOOLEAN      NOT NULL DEFAULT TRUE,   -- eliminacion logica
  CONSTRAINT chk_usuario_rol
    CHECK (rol IN ('ADMINISTRADO','REVISOR','TECNICO','SUBGERENTE','ADMIN'))
);

-- Actividades economicas (Indice de Usos). MANTENIMIENTO
CREATE TABLE giro (
  id_giro       INT AUTO_INCREMENT PRIMARY KEY,
  codigo_ciiu   VARCHAR(10)  NOT NULL UNIQUE,
  nombre        VARCHAR(150) NOT NULL,
  nivel_riesgo  VARCHAR(10)  NOT NULL,
  activo        BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_giro_riesgo
    CHECK (nivel_riesgo IN ('BAJO','MEDIO','ALTO','MUY_ALTO'))
);

-- Zonificacion del distrito. MANTENIMIENTO
CREATE TABLE zona (
  id_zona      INT AUTO_INCREMENT PRIMARY KEY,
  codigo       VARCHAR(10)  NOT NULL UNIQUE,     -- RDM, CV, CZ...
  nombre       VARCHAR(100) NOT NULL,
  descripcion  VARCHAR(255) NULL,
  activo       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Que giro esta permitido en que zona (si la fila existe, esta permitido)
CREATE TABLE giro_zona (
  id_giro INT NOT NULL,
  id_zona INT NOT NULL,
  PRIMARY KEY (id_giro, id_zona),
  FOREIGN KEY (id_giro) REFERENCES giro(id_giro),
  FOREIGN KEY (id_zona) REFERENCES zona(id_zona)
);

-- Para calcular los 15 dias habiles. MANTENIMIENTO
CREATE TABLE feriado (
  fecha        DATE         PRIMARY KEY,
  descripcion  VARCHAR(100) NOT NULL
);

-- El local fisico, con direccion estructurada (evita duplicados)
CREATE TABLE establecimiento (
  id_establecimiento INT AUTO_INCREMENT PRIMARY KEY,
  via_tipo    VARCHAR(10)   NOT NULL,            -- AV, JR, CALLE, PSJE
  via_nombre  VARCHAR(100)  NOT NULL,
  numero      VARCHAR(10)   NOT NULL,
  interior    VARCHAR(20)   NOT NULL DEFAULT '', -- '' y no NULL: ver explicacion
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
  -- El tramite es solo para locales de hasta 100 m2
  CONSTRAINT chk_area_maxima
    CHECK (area_m2 > 0 AND area_m2 <= 100)
);

-- La solicitud y todo su recorrido. Es el centro del sistema
CREATE TABLE expediente (
  id_expediente       INT AUTO_INCREMENT PRIMARY KEY,
  numero              VARCHAR(15)  NOT NULL UNIQUE,   -- 2026-000153
  id_administrado     INT          NOT NULL,
  id_establecimiento  INT          NOT NULL,
  id_giro             INT          NOT NULL,
  nombre_comercial    VARCHAR(150) NOT NULL,
  ruc                 CHAR(11)     NULL,              -- si es persona juridica
  razon_social        VARCHAR(150) NULL,
  estado              VARCHAR(20)  NOT NULL DEFAULT 'REGISTRADO',
  fecha_registro      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_limite        DATE         NOT NULL,          -- 15 dias habiles, lo calcula Java
  id_revisor          INT          NULL,
  id_tecnico_asignado INT          NULL,
  fecha_asignacion    DATETIME     NULL,
  -- Columna calculada: solo tiene valor si el expediente esta activo
  establecimiento_activo INT GENERATED ALWAYS AS (
    CASE WHEN estado IN ('REGISTRADO','VERIFICADO','OBSERVADO','EN_EVALUACION')
         THEN id_establecimiento END
  ) STORED,
  FOREIGN KEY (id_administrado)     REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_establecimiento)  REFERENCES establecimiento(id_establecimiento),
  FOREIGN KEY (id_giro)             REFERENCES giro(id_giro),
  FOREIGN KEY (id_revisor)          REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_tecnico_asignado) REFERENCES usuario(id_usuario),
  -- Un solo expediente activo por establecimiento
  CONSTRAINT uk_un_expediente_activo UNIQUE (establecimiento_activo),
  CONSTRAINT chk_expediente_estado CHECK (estado IN
    ('REGISTRADO','VERIFICADO','OBSERVADO','EN_EVALUACION','APROBADO','DENEGADO'))
);

-- Archivos subidos (carta poder legalizada, planos, etc.)
CREATE TABLE documento_adjunto (
  id_documento    INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente   INT          NOT NULL,
  tipo            VARCHAR(30)  NOT NULL,
  nombre_archivo  VARCHAR(255) NOT NULL,
  ruta            VARCHAR(500) NOT NULL,
  fecha_subida    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  CONSTRAINT chk_documento_tipo CHECK (tipo IN
    ('CARTA_PODER_LEGALIZADA','PLANO','OTRO'))
);

-- Observaciones del revisor o del tecnico (base del oficio en PDF)
CREATE TABLE observacion (
  id_observacion  INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente   INT          NOT NULL,
  id_usuario      INT          NOT NULL,          -- quien observa
  etapa           VARCHAR(15)  NOT NULL,
  numero_oficio   VARCHAR(20)  NOT NULL UNIQUE,
  descripcion     TEXT         NOT NULL,
  fecha           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subsanada       BOOLEAN      NOT NULL DEFAULT FALSE,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_usuario)    REFERENCES usuario(id_usuario),
  CONSTRAINT chk_observacion_etapa CHECK (etapa IN ('REVISION','EVALUACION'))
);

-- La licencia emitida (base del certificado en PDF)
CREATE TABLE licencia (
  id_licencia    INT AUTO_INCREMENT PRIMARY KEY,
  numero         VARCHAR(15) NOT NULL UNIQUE,
  id_expediente  INT         NOT NULL UNIQUE,     -- un expediente da una sola licencia
  id_firmante    INT         NOT NULL,            -- el Sub Gerente
  fecha_emision  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado         VARCHAR(10) NOT NULL DEFAULT 'VIGENTE',
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_firmante)   REFERENCES usuario(id_usuario),
  CONSTRAINT chk_licencia_estado CHECK (estado IN ('VIGENTE','REVOCADA'))
);

-- =====================================================================
-- PARTE 2: EXTRAS
-- =====================================================================

-- Quien tuvo cada expediente y por que (reparto equitativo)
CREATE TABLE historial_asignacion (
  id_historial     INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente    INT          NOT NULL,
  id_tecnico       INT          NOT NULL,
  id_asignado_por  INT          NOT NULL,
  tipo             VARCHAR(10)  NOT NULL,
  motivo           VARCHAR(255) NULL,
  fecha            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente)   REFERENCES expediente(id_expediente),
  FOREIGN KEY (id_tecnico)      REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_asignado_por) REFERENCES usuario(id_usuario),
  CONSTRAINT chk_asignacion_tipo CHECK (tipo IN ('AUTOMATICA','MANUAL'))
);

-- Pago con pasarela en modo de prueba
CREATE TABLE pago (
  id_pago              INT AUTO_INCREMENT PRIMARY KEY,
  id_expediente        INT           NOT NULL,
  monto                DECIMAL(10,2) NOT NULL,
  estado               VARCHAR(15)   NOT NULL DEFAULT 'PENDIENTE',
  referencia_pasarela  VARCHAR(100)  NULL,
  fecha                DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_expediente) REFERENCES expediente(id_expediente),
  CONSTRAINT chk_pago_estado CHECK (estado IN ('PENDIENTE','APROBADO','RECHAZADO'))
);

-- RENIEC simulado: DNI de prueba
CREATE TABLE persona_reniec (
  dni               CHAR(8)      PRIMARY KEY,
  nombres           VARCHAR(100) NOT NULL,
  apellidos         VARCHAR(100) NOT NULL,
  fecha_nacimiento  DATE         NOT NULL
);

-- Mapa: poligono de cada zona (solo si llegan al SIG)
-- ALTER TABLE zona ADD COLUMN poligono POLYGON SRID 4326 NULL;

-- =====================================================================
-- PARTE 3: DATOS DE PRUEBA
-- =====================================================================

-- Las contrasenas deben ser hash BCrypt generados por la aplicacion.
-- 'CAMBIAR' es un marcador: registren los usuarios desde el sistema
-- o reemplacen por un hash generado con BCryptPasswordEncoder.
INSERT INTO usuario (dni, nombres, apellidos, correo, password, rol) VALUES
('10000001','Admin','Sistema','admin@muni.pe','CAMBIAR','ADMIN'),
('10000002','Rosa','Quispe','revisor@muni.pe','CAMBIAR','REVISOR'),
('10000003','Carlos','Ruiz','tecnico1@muni.pe','CAMBIAR','TECNICO'),
('10000004','Lucia','Torres','tecnico2@muni.pe','CAMBIAR','TECNICO'),
('10000005','Jorge','Mendoza','subgerente@muni.pe','CAMBIAR','SUBGERENTE'),
('10000006','Juan','Perez','juan.perez@correo.com','CAMBIAR','ADMINISTRADO');

INSERT INTO zona (codigo, nombre, descripcion) VALUES
('RDM','Residencial de Densidad Media','Vivienda con comercio local permitido'),
('CV','Comercio Vecinal','Comercio diario de pequena escala'),
('CZ','Comercio Zonal','Comercio de mayor escala'),
('I1','Industria Elemental','Talleres y actividades complementarias');

INSERT INTO giro (codigo_ciiu, nombre, nivel_riesgo) VALUES
('4711','Bodega',                 'BAJO'),
('5610','Restaurante',            'MEDIO'),
('9602','Peluqueria',             'BAJO'),
('4520','Taller mecanico',        'ALTO');

-- Indice de Usos: bodega (1) en RDM, CV, CZ; restaurante (2) en CV, CZ;
-- peluqueria (3) en RDM, CV, CZ; taller (4) en CZ, I1
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
