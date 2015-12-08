--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.107

ALTER TABLE cf_categorydiscounts ADD COLUMN organizationtype integer NOT NULL DEFAULT -1;

--Таблица для хранения событий, принятых от ЭЖД
CREATE TABLE cf_enterevents_manual
(
  idofentereventmanual bigserial NOT NULL,
  idoforg bigint NOT NULL,
  entername character varying(100) NOT NULL,
  idofclient bigint NOT NULL,
  evtdatetime bigint NOT NULL,
  CONSTRAINT cf_enterevents_manual_pk PRIMARY KEY (idofentereventmanual),
  CONSTRAINT cf_enterevents_manual_clientguid_evtdatetime_key UNIQUE (idofclient, evtdatetime)
)
WITH (
OIDS=FALSE
);

CREATE INDEX cf_enterevents_manual_evtdatetime_idx ON cf_enterevents_manual USING btree (evtdatetime);

CREATE INDEX cf_enterevents_manual_idofclient_idx ON cf_enterevents_manual USING btree (idofclient);

-- связка пользователи организации по рассылкам увидомлений по изменнным заказам
CREATE TABLE CF_JobRules
(
  idOfJobRule bigserial NOT NULL,
  idOfReportHandleRule bigint NOT NULL,
  idOfSchedulerJob bigint NOT NULL,
  CONSTRAINT cf_IdOfJobRule_pk PRIMARY KEY (idOfJobRule),
  CONSTRAINT cf_JobRules_ReportHandleRule_fk FOREIGN KEY (idOfReportHandleRule) REFERENCES CF_ReportHandleRules (idOfReportHandleRule),
  CONSTRAINT cf_JobRules_SchedulerJob_fk FOREIGN KEY (idOfSchedulerJob) REFERENCES CF_SchedulerJobs (idOfSchedulerJob),
  CONSTRAINT cf_JobRules_uq UNIQUE (idOfReportHandleRule, idOfSchedulerJob)
);