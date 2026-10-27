-- Ejecutar conectado a la base administrativa "postgres", no dentro de riesgos_ra.
-- Ejecutar solamente si la base riesgos_ra aun NO existe. CREATE DATABASE no puede ir
-- dentro de una transaccion. Si ya existe, omitir este archivo.
CREATE DATABASE riesgos_ra WITH ENCODING 'UTF8';
