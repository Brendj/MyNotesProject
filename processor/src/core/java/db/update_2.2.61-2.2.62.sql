--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.62

ALTER TABLE cf_subscriber_feeding ALTER COLUMN dateActivateService DROP NOT NULL;
ALTER TABLE cf_subscriber_feeding ADD COLUMN dateCreateService BIGINT;
UPDATE cf_subscriber_feeding SET dateCreateService=dateActivateService;
-- ALTER TABLE cf_subscriber_feeding ADD COLUMN reasonWasSuspended CHARACTER VARYING(1024);

CREATE TABLE cf_UserOrgs
(
  IdOfUserOrg bigserial NOT NULL,
  idOfUser bigint NOT NULL,
  idOfOrg bigint NOT NULL,
  CONSTRAINT cf_UserOrg_pk PRIMARY KEY (IdOfUserOrg),
  CONSTRAINT cf_UserOrgs_User_fk FOREIGN KEY (idOfUser) REFERENCES cf_users (IdOfUser),
  CONSTRAINT cf_UserOrgs_Org_fk FOREIGN KEY (idOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_userorgs_uq UNIQUE (idofuser, idoforg)
);

CREATE TABLE cf_Registry_Talon
(
  idOfRegistryTalon BIGSERIAL NOT NULL,
  OrgOwner BIGINT,
  GUID CHARACTER VARYING(36) NOT NULL,
  DeletedState BOOLEAN NOT NULL DEFAULT FALSE,
  GlobalVersion BIGINT,
  GlobalVersionOnCreate BIGINT DEFAULT NULL,
  CreatedDate BIGINT NOT NULL,
  LastUpDate BIGINT,
  DeleteDate BIGINT,
  SendAll INTEGER NOT NULL DEFAULT 0,
  Date BIGINT,
  Number BIGINT,
  CONSTRAINT cf_registry_talon_pk PRIMARY KEY (idOfRegistryTalon)
);

--
alter table cf_contragents add column OrderNotifyMailList text default null;

-- Сучность сохранения отменных заказов
CREATE TABLE CF_CanceledOrders (
  idOfCanceledOrder BIGSERIAL   NOT NULL,
  IdOfOrg           BIGINT        NOT NULL,
  IdOfOrder         BIGINT        NOT NULL,
  idOfTransaction   BIGINT        ,
  CreatedDate       BIGINT        NOT NULL,
  CONSTRAINT CF_CanceledOrders_pk PRIMARY KEY (idOfCanceledOrder),
  CONSTRAINT CF_CanceledOrders_IdOfOrg_fk FOREIGN KEY (IdOfOrg)
  REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_CanceledOrders_IdOfOrg_IdOfOrder_fk FOREIGN KEY (IdOfOrg, IdOfOrder)
  REFERENCES CF_Orders (IdOfOrg, IdOfOrder),
  CONSTRAINT CF_CanceledOrders_IdOfOrg_idOfTransaction_fk FOREIGN KEY (idOfTransaction)
  REFERENCES CF_Transactions(idOfTransaction)
);

create index CF_CanceledOrders_fk_idx on CF_CanceledOrders(idoforg, idoforder);
