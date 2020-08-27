--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 221

CREATE TABLE cf_hardware_settings
(
    idoforg                int8 NOT NULL,
    iphost                 text NOT NULL,
    lastupdateforiphost    int8 NOT NULL,
    dotnetver              text NOT NULL,
    lastupdatefordotnetver int8 NOT NULL,
    osver                  text NOT NULL,
    lastupdateforosver     int8 NOT NULL,
    ramsize                text NOT NULL,
    lastupdateforramsize   int8 NOT NULL,
    cpuhost                text NOT NULL,
    lastupdateforcpuhost   int8 NOT NULL,
    "version"              int8 NOT NULL,
    idofhardwaresetting    int8 NOT NULL,
    CONSTRAINT cf_hardware_settings_idofhardwaresetting_pk PRIMARY KEY (idofhardwaresetting, idoforg),
    CONSTRAINT cf_hardwarese_settings_unique_ip_org UNIQUE (idoforg, iphost),
    CONSTRAINT cf_hardware_settings_idoforg_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg)
);

CREATE TABLE cf_hardware_settings_mt
(
    idofhardwaresetting int8 NOT NULL,
    idofmoduletype      int8 NOT NULL,
    moduletype          int4 NOT NULL,
    installstatus       int4 NOT NULL,
    readername          text NULL,
    firmwarever         text NULL,
    lastupdate          int8 NOT NULL,
    idoforg             int8 NOT NULL,
    CONSTRAINT cf_hardware_settings_mt_idofmoduletype_pk PRIMARY KEY (idofmoduletype),
    CONSTRAINT cf_hardware_settigs_mt_idofhardwaresetting_fk FOREIGN KEY (idofhardwaresetting, idoforg) REFERENCES cf_hardware_settings (idofhardwaresetting, idoforg)
);

CREATE TABLE cf_turnstile_settings
(
    idofturnstilesetting      int8         NOT NULL,
    idoforg                   int8         NOT NULL,
    "version"                 int8         NOT NULL,
    numofentries              int8         NOT NULL,
    turnstileid               varchar(64)  NOT NULL,
    controllermodel           varchar(256) NULL,
    controllerfirmwareversion varchar(64)  NULL,
    isreadslongidsincorrectly int4         NOT NULL,
    lastupdateforturnstile    int8         NOT NULL,
    timecoefficient           float4       NOT NULL,
    CONSTRAINT cf_hardware_settings_turnstileid_pk PRIMARY KEY (turnstileid, idoforg),
    CONSTRAINT cf_hardware_settings_idoforg_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg)
);

CREATE SEQUENCE cf_hardwaresettings_idofhardwaresetting_mt_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
MAXVALUE 2147483647
CACHE 1;

CREATE SEQUENCE cf_turnstilesettings_idofturnstilesetting_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
MAXVALUE 2147483647
CACHE 1;

--! ФИНАЛИЗИРОВАН 06.08.2020, НЕ МЕНЯТЬ