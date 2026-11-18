-- Revision de contenido RiesgoRA 17/11/2026. Conserva IDs y nombres.
BEGIN;
UPDATE public.informacion_riesgos SET analisis_riesgo='El humo y el fuego pueden causar quemaduras e impedir la salida. Las fallas eléctricas y los materiales inflamables aumentan el riesgo.', plan_accion='Revisar extintores e instalaciones eléctricas, practicar la evacuación y mantener libres las salidas.', consejos_estudiante='Si detectas humo o fuego, avisa inmediatamente a un docente.
Mantén la calma y camina sin correr ni empujar.
Sigue la ruta de evacuación indicada por el personal responsable.' WHERE lower(nombre) LIKE '%incend%';
UPDATE public.informacion_riesgos SET analisis_riesgo='Un sismo puede hacer caer objetos, romper vidrios y dañar la estructura. Aléjate de ventanas y muebles altos.', plan_accion='Asegurar estanterías, colocar objetos pesados abajo e identificar rutas y puntos de encuentro.', consejos_estudiante='Si no hay un refugio resistente cerca, agáchate junto a una pared interior, lejos de ventanas y muebles altos.
Protege tu cabeza y cuello con las manos y los antebrazos.
Mantén la calma y permanece protegido mientras dure el movimiento.
Cuando termine el movimiento, sigue las indicaciones del personal responsable.' WHERE lower(nombre) LIKE '%sism%';
UPDATE public.informacion_riesgos SET analisis_riesgo='El agua acumulada puede bloquear el paso y dañar las aulas. También puede ocultar desniveles o riesgos eléctricos.', plan_accion='Limpiar desagües, guardar materiales en alto e identificar una ruta de evacuación alejada del agua.', consejos_estudiante='No camines por zonas inundadas.
Mantente alejado de cables eléctricos.
Comunica inmediatamente la situación a una persona encargada o al docente más cercano.' WHERE lower(nombre) LIKE '%inund%';
COMMIT;
SELECT id, nombre, consejos_estudiante FROM public.informacion_riesgos ORDER BY id;
