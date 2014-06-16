--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.66

-- таблица блокирования заказов
CREATE TABLE cf_Prohibitions
(
  IdOfProhibitions bigserial NOT NULL,
  IdOfClient bigint NOT NULL,
--  IdOfOrg bigint NOT NULL,
  FilterText character varying(1024) NOT NULL,
  ProhibitionFilterType int NOT NULL,
  CreateDate bigint NOT NULL,
  UpdateDate bigint,
  Version bigint,
  deletedState boolean NOT NULL DEFAULT false,
  CONSTRAINT cf_Prohibitions_pk PRIMARY KEY (IdOfProhibitions),
  CONSTRAINT cf_Prohibitions_Client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
--  CONSTRAINT cf_Prohibitions_Org_fk FOREIGN KEY (idOfOrg) REFERENCES cf_orgs (IdOfOrg)
);

CREATE INDEX cf_Prohibitions_idOfClient_idx on cf_Prohibitions USING btree (IdOfClient);
--CREATE INDEX cf_ProhibitionsOrg_fk_idx on cf_Prohibitions USING btree (IdOfOrg);
