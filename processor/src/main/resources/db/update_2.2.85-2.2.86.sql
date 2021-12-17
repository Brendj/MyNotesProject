--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.85

--! Таблица "Агрегирование данных отчет Xml"
CREATE TABLE cf_daily_formation_registries (
  idOfDailyFormationRegistries BIGSERIAL,
  generatedDate BIGINT NOT NULL,
  idOfContragent BIGINT NOT NULL,
  contragentName VARCHAR(255),
  orgNum VARCHAR(30),
  idOfOrg BIGINT NOT NULL,
  officialName VARCHAR(128),
  address VARCHAR(128),
  totalBalance BIGINT NOT NULL,
  rechargeAmount BIGINT NOT NULL,
  salesAmount BIGINT NOT NULL,
  CONSTRAINT cf_daily_formation_registries_pk PRIMARY KEY (idOfDailyFormationRegistries)
);


--  Таблица для хранения СМС, которые необходимо переслать. СМС помещаются в данную таблицу,
--  если по какой-то причине не удалось их отправить
--! ContentsId - идентификатор события (продажи, пополнения, прохода и т.п.)
--! ContentsType - тип события, который можно будет найти по идентификатору (ContentsId)
--! ServiceName - наименование сервиса, через который осуществлялась попытка отправки СМС
CREATE TABLE cf_clientsms_resending (
  IdOfSms                 CHAR(40)         NOT NULL,
  Version                 BIGINT NOT NULL,
  idOfClient              BIGINT NOT NULL,
  Phone                   VARCHAR(32),
  ServiceName             VARCHAR(10),
  ContentsId              BIGINT NOT NULL,
  ContentsType            INTEGER          NOT NULL,
  TextContents            VARCHAR(120)     NOT NULL,
  ParamsContents          VARCHAR(255)     NOT NULL,
  CreateDate              BIGINT,
  LastResendingDate       BIGINT,

  CONSTRAINT cf_clientsms_resending_pk PRIMARY KEY (IdOfSms, ServiceName),
  CONSTRAINT cf_clientsms_resending_idofclient_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);
CREATE INDEX cf_clientsms_resending_date_idx on cf_clientsms_resending(CreateDate);

-- Добавление указания идентификатора источника события для отправки СМС
alter table cf_clientsms add column ContentsId BIGINT DEFAULT NULL;

--! ФИНАЛИЗИРОВАН (Сунгатов, 141222) НЕ МЕНЯТЬ
