BEGIN;
CREATE TABLE IF NOT EXISTS public.informacion_riesgos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    analisis_riesgo TEXT NOT NULL,
    plan_accion TEXT NOT NULL,
    consejos_estudiante TEXT NOT NULL
);
INSERT INTO public.informacion_riesgos (nombre, analisis_riesgo, plan_accion, consejos_estudiante)
SELECT v.nombre, v.analisis_riesgo, v.plan_accion, v.consejos_estudiante
FROM (VALUES (
    'Prevención de Incendios',
    'Un incendio escolar puede originarse por cortocircuitos, acumulación de materiales inflamables o instalaciones de gas deficientes. La propagación puede ser rápida y poner en riesgo a estudiantes, docentes e infraestructura.',
    'Mantener extintores vigentes, realizar simulacros periódicos, revisar las instalaciones eléctricas y mantener libres las rutas de evacuación.',
    'Si detectas humo o fuego, avisa inmediatamente a un profesor, mantén la calma, no corras y sigue la ruta de evacuación establecida.'
),
(
    'Prevención de Sismo',
    'Los movimientos sísmicos pueden comprometer estructuras no reforzadas. El riesgo incluye caída de objetos, rotura de vidrios y posibles daños en las instalaciones.',
    'Identificar previamente las zonas de menor riesgo, asegurar estanterías, retirar objetos pesados ubicados en lugares altos y mantener despejadas las rutas de evacuación.',
    'Durante un sismo, busca protección bajo una mesa o escritorio resistente, protege tu cabeza, mantén la calma y sigue las indicaciones del personal responsable.'
),
(
    'Prevención de Inundaciones',
    'Las inundaciones pueden producirse por lluvias extremas o por el colapso del sistema de alcantarillado. Pueden provocar daños en instalaciones, materiales y equipos.',
    'Mantener limpias las canaletas y desagües, proteger documentos y materiales en lugares elevados y contar con rutas seguras de evacuación.',
    'No camines por zonas inundadas, mantente alejado de cables eléctricos y comunica inmediatamente cualquier acumulación de agua.'
)) AS v(nombre, analisis_riesgo, plan_accion, consejos_estudiante)
WHERE NOT EXISTS (SELECT 1 FROM public.informacion_riesgos r WHERE r.nombre = v.nombre);
COMMIT;
