--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.67

-- таблица блокирования заказов
CREATE TABLE cf_Prohibitions
(
  IdOfProhibitions bigserial NOT NULL,
  IdOfClient bigint NOT NULL,
  FilterText character varying(1024) NOT NULL,
  ProhibitionFilterType int NOT NULL,
  CreateDate bigint NOT NULL,
  UpdateDate bigint,
  Version bigint,
  deletedState boolean NOT NULL DEFAULT false,
  CONSTRAINT cf_Prohibitions_pk PRIMARY KEY (IdOfProhibitions),
  CONSTRAINT cf_Prohibitions_Client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

CREATE INDEX cf_Prohibitions_idOfClient_idx on cf_Prohibitions USING btree (IdOfClient);
CREATE INDEX cf_Prohibitions_version_idx on cf_Prohibitions USING btree (Version);

ALTER TABLE cf_subscriber_feeding ADD column IdOfStaff bigint;
ALTER TABLE cf_subscriber_feeding ADD constraint cf_service_subscriber_feeding_staff_fk FOREIGN KEY (IdOfStaff) REFERENCES cf_staffs (IdOfStaff);

ALTER TABLE cf_clients_cycle_diagrams ADD column idOfStaff bigint;
ALTER TABLE cf_clients_cycle_diagrams ADD constraint cf_clients_cycle_diagrams_staff_fk FOREIGN KEY (IdOfStaff) REFERENCES cf_staffs (IdOfStaff);
--! ФИНАЛИЗИРОВАН (Кадыров, 140624) НЕ МЕНЯТЬ