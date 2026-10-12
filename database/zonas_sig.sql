-- =====================================================================
-- SIG: cargar las zonas dibujadas en geojson.io y probar la deteccion
-- Requisito: haber ejecutado antes licencias_db.sql
-- =====================================================================
USE licencias_db;

-- 1. Agregar la columna del poligono a la tabla zona
ALTER TABLE zona ADD COLUMN poligono POLYGON SRID 4326 NULL;

-- 2. Agregar las zonas que faltaban en los datos de prueba
INSERT INTO zona (codigo, nombre, descripcion) VALUES
('ZRP', 'Zona de Recreacion Publica', 'Parques y areas verdes'),
('E1',  'Educacion Basica',           'Colegios');

-- 3. Cargar cada poligono desde su GeoJSON (solo la parte "geometry")
UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0627104,-11.9855344],[-77.0612529,-11.9852987],[-77.0613179,-11.9848653],
  [-77.060997,-11.9848316],[-77.0611152,-11.9841215],[-77.0628743,-11.9843945],
  [-77.0627924,-11.9849288],[-77.0627104,-11.9855344]]]}')
WHERE codigo = 'ZRP';

UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0626198,-11.9838406],[-77.0612001,-11.9836128],[-77.0611162,-11.9841215],
  [-77.0625405,-11.9843419],[-77.0626198,-11.9838406]]]}')
WHERE codigo = 'E1';

UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.062704,-11.985579],[-77.0626294,-11.9860009],[-77.060843,-11.9857218],
  [-77.0609134,-11.9852995],[-77.0618085,-11.9854426],[-77.062704,-11.985579]]]}')
WHERE codigo = 'CZ';

UPDATE zona SET poligono = ST_GeomFromGeoJSON('{"type":"Polygon","coordinates":[[
  [-77.0605601,-11.9842799],[-77.0604857,-11.9847402],[-77.0590323,-11.9845487],
  [-77.0591334,-11.9840759],[-77.0593063,-11.9841055],[-77.0605601,-11.9842799]]]}')
WHERE codigo = 'RDM';

-- 4. Verificar que las 4 zonas tengan poligono y que sean validos
SELECT codigo, nombre, ST_IsValid(poligono) AS valido
FROM zona
WHERE poligono IS NOT NULL;

-- 5. PRUEBA: en que zona cae un punto
--    Punto dentro del parque: debe devolver ZRP
SELECT codigo, nombre
FROM zona
WHERE ST_Contains(poligono,
      ST_GeomFromText('POINT(-77.0620 -11.9848)', 4326, 'axis-order=long-lat'));

--    Punto dentro de la CZ (sobre Av. Las Almendras): debe devolver CZ
SELECT codigo, nombre
FROM zona
WHERE ST_Contains(poligono,
      ST_GeomFromText('POINT(-77.0618 -11.98565)', 4326, 'axis-order=long-lat'));

--    Punto fuera de todas las zonas: no debe devolver nada
SELECT codigo, nombre
FROM zona
WHERE ST_Contains(poligono,
      ST_GeomFromText('POINT(-77.0640 -11.9848)', 4326, 'axis-order=long-lat'));
