--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.108

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