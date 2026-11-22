-- Datos ficticios para probar la app apenas se levanta el proyecto.
-- USR-001 es el docente de prueba y USR-002 el estudiante de prueba.

INSERT INTO sectores (nombre, descripcion, ubicacion, estado, responsable_id, fecha_creacion, fecha_actualizacion) VALUES
    ('Invernadero', 'Sector techado para almácigos', 'Patio norte', 'EN_USO', 'USR-001', '2026-09-01', '2026-09-01'),
    ('Bancal de hortalizas', 'Bancal elevado de madera', 'Patio central', 'EN_USO', 'USR-001', '2026-09-01', '2026-09-01'),
    ('Compostera', 'Zona de compostaje del taller', 'Junto a la bodega', 'EN_MANTENIMIENTO', 'USR-001', '2026-09-01', '2026-09-01');

INSERT INTO cultivos (nombre_comun, descripcion, sector_id, fecha_siembra, fecha_trasplante, fecha_estimada_cosecha, frecuencia_riego_dias, estado, observaciones) VALUES
    ('Lechuga', 'Lechuga escarola', 1, '2026-09-15', '2026-10-10', '2026-12-05', 2, 'EN_CRECIMIENTO', 'Crece bien con sombra parcial'),
    ('Tomate', 'Tomate cherry', 2, '2026-09-20', NULL, '2027-01-15', 3, 'SEMBRADO', NULL);

INSERT INTO tareas (tipo, descripcion, sector_id, cultivo_id, fecha_creacion, fecha_programada, responsable_id, creador_id, estado) VALUES
    ('RIEGO', 'Regar las lechugas del invernadero', 1, 1, '2026-11-01', '2026-11-25', 'USR-002', 'USR-001', 'PENDIENTE'),
    ('RETIRO_MALEZAS', 'Sacar malezas del bancal de tomates', 2, 2, '2026-11-01', '2026-11-28', 'USR-002', 'USR-001', 'PENDIENTE');
