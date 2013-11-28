
 --1. Открываем pgAdmin
 --2. Щелкаем по базе postgres
 --3. Выполняем SQL запросы по одному

-- Database: ecafe_clientroom_db

 DROP DATABASE IF EXISTS ecafe_clientroom_db;

CREATE DATABASE ecafe_clientroom_db
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Russian_Russia.1251'
       LC_CTYPE = 'Russian_Russia.1251'
       CONNECTION LIMIT = -1;


 --4. Щелкаем по базе ecafe_clientroom_db
 --5. Выполняем оставшиеся SQL запросы, все сразу

--  -- Table: cf_authorization_types

 --DROP TABLE IF EXISTS cf_authorization_types;

CREATE TABLE cf_authorization_types
(
  idofauthorizationtype bigserial NOT NULL,
  name character varying(64),
  CONSTRAINT cf_authorization_types_pkey PRIMARY KEY (idofauthorizationtype )
)
WITH (
  OIDS=FALSE
);

INSERT INTO cf_authorization_types(idofauthorizationtype, name) VALUES(1, 'BASIC');
INSERT INTO cf_authorization_types(idofauthorizationtype, name) VALUES(2, 'SSL');

-- Table: cf_cities

 --DROP TABLE IF EXISTS cf_cities;

CREATE TABLE cf_cities
(
  idofcity bigserial NOT NULL,
  activity boolean NOT NULL,
  name character varying(128),
  serviceurl character varying(128),
  contractidmask character varying(128),
  username character varying(128),
  password character varying(128),
  idofauthorizationtype bigint NOT NULL,
  CONSTRAINT cf_cities_pkey PRIMARY KEY (idofcity )
)
WITH (
  OIDS=FALSE
);
 INSERT INTO cf_cities(idofcity, activity, name, serviceurl,contractidmask, username,password,idofauthorizationtype)VALUES (1, true, 'Москва', 'http://localhost:8080/processor/soap/client', '', '', '', 1);



-- Table: cf_functions

 --DROP TABLE IF EXISTS cf_functions;

CREATE TABLE cf_functions
(
  idoffunction bigint NOT NULL,
  functionname character varying(10),
  CONSTRAINT cf_functions_pkey PRIMARY KEY (idoffunction )
)
WITH (
  OIDS=FALSE
);




 -- Table: cf_users

 --DROP TABLE IF EXISTS cf_users;

CREATE TABLE cf_users
(
  username character varying(64),
  password character varying(128),
  phone character varying(32),
  email character varying(128),
  idofuser bigint NOT NULL,
  version bigint NOT NULL,
  lastchange bigint NOT NULL,
  CONSTRAINT cf_users_pkey PRIMARY KEY (idofuser ),
  CONSTRAINT cf_users_username_key UNIQUE (username )
)
WITH (
  OIDS=FALSE
);


 INSERT INTO cf_users(idofuser, username, password, phone, email, version, lastchange)VALUES (1, 'admin', '0DPiKuNIrrVmD8IUCuw1hQxNqZc=', '', '', 0, 1346841836482);



 -- Table: cf_permissions

 --DROP TABLE IF EXISTS cf_permissions;

CREATE TABLE cf_permissions
(
  idofuser bigint NOT NULL,
  idoffunction bigint NOT NULL,
  CONSTRAINT cf_permissions_pkey PRIMARY KEY (idofuser , idoffunction ),
  CONSTRAINT cf_permissions_idoffunction_fkey FOREIGN KEY (idoffunction)
      REFERENCES cf_functions (idoffunction) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_permissions_idofuser_fkey FOREIGN KEY (idofuser)
      REFERENCES cf_users (idofuser) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

