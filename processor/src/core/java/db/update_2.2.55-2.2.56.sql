--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.56
--! В настоящий момент таблица не используется, можно удалить и создать заново, но лучше изменить.
drop table cf_temporary_orders;
CREATE TABLE cf_temporary_orders (
  IdOfOrg bigint not null,
  IdOfClient bigInt not null,
  IdOfComplex int not null,
  IdOfRule bigint not null,
  PlanDate bigint not null,
  Action int not null,
  IdOfReplaceClient bigInt,
  CreationDate bigint not null,
  ModificationDate bigint,
  IdOfOrder bigint default null,
  IdOfUser bigint not null,
  InBuilding int not null default 2,
  CONSTRAINT cf_temporary_orders_pk PRIMARY KEY (IdOfOrg, IdOfClient, IdOfComplex, PlanDate, IdOfRule),
  CONSTRAINT cf_temporary_orders_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_temporary_orders_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);