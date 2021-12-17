--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.35

CREATE TABLE CF_ClientsNotificationSettings
(
  IdOfSetting   bigserial                      NOT NULL,
  IdOfClient    bigint                         NOT NULL,
  NotifyType    bigint                         NOT NULL,
  CreatedDate   bigint                         NOT NULL,

  CONSTRAINT CF_ClientsSMSSetting_PK           PRIMARY KEY (IdOfSetting),
  CONSTRAINT CF_ClientsSMSSetting_NotifyPair   UNIQUE      (IdOfClient, NotifyType)
);

update cf_projectstate_data set region='Все округа' where region='Все';

--! ФИНАЛИЗИРОВАН (Кадыров, 130218) НЕ МЕНЯТЬ