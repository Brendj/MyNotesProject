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