-- Flyway ejecuta este script una sola vez, la primera vez que se levanta el proyecto.
-- Si después necesitas cambiar una tabla, crea un script nuevo (V3__..., V4__...) en vez de editar este.

CREATE TABLE sectores (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(40)  NOT NULL,
    descripcion         VARCHAR(200),
    ubicacion           VARCHAR(80)  NOT NULL,
    estado              VARCHAR(30)  NOT NULL,
    responsable_id      VARCHAR(20),
    fecha_creacion      DATE         NOT NULL,
    fecha_actualizacion DATE,
    PRIMARY KEY (id)
);

CREATE TABLE cultivos (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre_comun           VARCHAR(40)  NOT NULL,
    descripcion            VARCHAR(200),
    sector_id              BIGINT       NOT NULL,
    fecha_siembra          DATE,
    fecha_trasplante       DATE,
    fecha_estimada_cosecha DATE,
    frecuencia_riego_dias  INT          NOT NULL,
    estado                 VARCHAR(30)  NOT NULL,
    observaciones          VARCHAR(300),
    PRIMARY KEY (id),
    CONSTRAINT fk_cultivos_sector FOREIGN KEY (sector_id) REFERENCES sectores (id)
);

CREATE TABLE tareas (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    tipo                   VARCHAR(30)  NOT NULL,
    descripcion            VARCHAR(150) NOT NULL,
    sector_id              BIGINT       NOT NULL,
    cultivo_id             BIGINT,
    fecha_creacion         DATE         NOT NULL,
    fecha_programada       DATE         NOT NULL,
    responsable_id         VARCHAR(20),
    creador_id             VARCHAR(20),
    estado                 VARCHAR(30)  NOT NULL,
    fecha_hora_realizacion DATETIME,
    fotografia             VARCHAR(500),
    comentario_estudiante  VARCHAR(300),
    comentario_docente     VARCHAR(300),
    validador_id           VARCHAR(20),
    fecha_hora_validacion  DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_tareas_sector  FOREIGN KEY (sector_id)  REFERENCES sectores (id),
    CONSTRAINT fk_tareas_cultivo FOREIGN KEY (cultivo_id) REFERENCES cultivos (id)
);

CREATE TABLE problemas (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    sector_id          BIGINT       NOT NULL,
    cultivo_id         BIGINT,
    tipo               VARCHAR(30)  NOT NULL,
    descripcion        VARCHAR(200) NOT NULL,
    fecha_hora_reporte DATETIME     NOT NULL,
    autor_id           VARCHAR(20),
    fotografia         VARCHAR(500),
    estado             VARCHAR(30)  NOT NULL,
    comentario_docente VARCHAR(200),
    fecha_cierre       DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_problemas_sector  FOREIGN KEY (sector_id)  REFERENCES sectores (id),
    CONSTRAINT fk_problemas_cultivo FOREIGN KEY (cultivo_id) REFERENCES cultivos (id)
);
