--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.112

--Справочник типов операция для журнала обращений к сервису информирования
CREATE TABLE cf_log_infoservice_operation_types
(
  idofoperationtype BIGSERIAL NOT NULL,
  nameofoperationtype character varying(50),
  CONSTRAINT cf_log_infoservice_operation_types_pk PRIMARY KEY (idofoperationtype)
);

--Таблица журналирования обращений к сервису информирования
CREATE TABLE cf_log_infoservice
(
  idofloginfoservice BIGSERIAL NOT NULL,
  idofsystem character varying(20),
  createddate BIGINT NOT NULL,
  ssoid character varying(20),
  idofclient BIGINT,
  idofoperationtype BIGINT NOT NULL,
  CONSTRAINT cf_log_infoservice_pk PRIMARY KEY (idofloginfoservice),
  CONSTRAINT cf_log_infoservice_idoftype_fk FOREIGN KEY (idofoperationtype)
  REFERENCES cf_log_infoservice_operation_types(idofoperationtype) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--! ФИНАЛИЗИРОВАН (Семенов, 300316) НЕ МЕНЯТЬ