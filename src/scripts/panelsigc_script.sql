--
-- PostgreSQL database script
--
CREATE USER panelsigc;
ALTER USER panelsigc with password 'panelsigc';


--
-- DATABASE
--
CREATE DATABASE panelsigc WITH OWNER = panelsigc
    ENCODING = 'UTF8';

-- connection database
\connect panelsigc;

--
-- SCHEMA
--
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO panelsigc;

--
-- TABLES
---
-- databases
CREATE TABLE databases (
  x_database bigint NOT NULL,
  t_alias character varying(30),
  t_database_url character varying(150),
  f_creation_date date,
  CONSTRAINT databases_pkey PRIMARY KEY (x_database)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE databases OWNER TO panelsigc;

-- schemas
CREATE TABLE schemas (
  x_schema bigint NOT NULL,
  f_creation_date date,
  t_schema character varying(50),
  t_password character varying(30),
  t_user character varying(30),
  x_database bigint NOT NULL,
  CONSTRAINT schemas_pkey PRIMARY KEY (x_schema)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schemas OWNER TO panelsigc;

-- services
CREATE TABLE services (
  x_service bigint NOT NULL,
  f_creation_date date,
  t_name character varying(50),
  t_service_url character varying(250),
  sety_x_service_type bigint NOT NULL,
  CONSTRAINT services_pkey PRIMARY KEY (x_service)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE services OWNER TO panelsigc;

-- services_dependences
CREATE TABLE services_dependences (
  services_x_service_parent bigint NOT NULL,
  services_x_service_child bigint NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE services_dependences OWNER TO panelsigc;

-- services_types
CREATE TABLE services_types (
  x_service_type bigint NOT NULL,
  t_acronym character varying(50),
  f_creation_date date,
  t_description character varying(250),
  CONSTRAINT services_types_pkey PRIMARY KEY (x_service_type)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE services_types OWNER TO panelsigc;

-- tables
CREATE TABLE tables (
  x_table bigint NOT NULL,
  f_creation_date date,
  t_epsg character varying(10),
  t_geom_field character varying(50),
  f_modification_date date,
  t_table_name character varying(50),
  x_schema bigint NOT NULL,
  CONSTRAINT tables_pkey PRIMARY KEY (x_table)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tables OWNER TO panelsigc;

-- tables_x_services
CREATE TABLE tables_x_services (
  x_tablexservi bigint NOT NULL,
  f_creation_date date,
  services_x_service bigint NOT NULL,
  tables_x_table bigint NOT NULL,
  CONSTRAINT tables_x_services_pkey PRIMARY KEY (x_tablexservi)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tables_x_services OWNER TO panelsigc;

-- tasks
CREATE TABLE tasks (
  x_task bigint NOT NULL,
  t_repository_alias character varying(250),
  t_source_content_type character varying(150),
  t_divider character varying(5),
  t_source_name character varying(150),
  n_source_num_lines integer NOT NULL,
  t_repository_password character varying(150),
  l_remote integer NOT NULL,
  n_source_size integer,
  type character varying(255),
  t_repository_url character varying(250),
  t_repository_user character varying(150),
  description character varying(250),
  progress double precision,
  n_read_lines integer NOT NULL,
  status character varying(255),
  t_ticket character varying(250) NOT NULL,
  l_update integer NOT NULL,
  tables_x_table bigint NOT NULL,
  x_table bigint,
  CONSTRAINT tasks_pkey PRIMARY KEY (x_task),
  CONSTRAINT tasks_t_ticket_key UNIQUE (t_ticket)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tasks OWNER TO panelsigc;

--
-- Data for services_types
--
INSERT INTO services_types (x_service_type, t_acronym, f_creation_date, t_description) VALUES (1, 'WMS', null, 'Servicios WMS del estándar OGC');
INSERT INTO services_types (x_service_type, t_acronym, f_creation_date, t_description) VALUES (2, 'WFS', null, 'Servicios WFS del estándar OGC');
INSERT INTO services_types (x_service_type, t_acronym, f_creation_date, t_description) VALUES (3, 'RA', null, 'Servicio de realidad aumentada');
INSERT INTO services_types (x_service_type, t_acronym, f_creation_date, t_description) VALUES (4, 'GEOBUSQUEDAS', null, 'Servicio de GeoBúsquedas');