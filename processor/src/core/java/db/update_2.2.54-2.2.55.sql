--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.55

CREATE TABLE cf_client_guardian
(
  IdOfClientGuardian bigserial NOT NULL,
  Version bigint NOT NULL,
  IdOfChildren bigint NOT NULL,
  IdOfGuardian bigint NOT NULL,
  GuardianType integer DEFAULT 0,
  CONSTRAINT cf_client_guardian_pk PRIMARY KEY (IdOfClientGuardian),
  CONSTRAINT cf_client_guardian_children_fk FOREIGN KEY (IdOfChildren)
  REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_client_guardian_guardian_fk FOREIGN KEY (IdOfGuardian)
  REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_client_guardian_uk UNIQUE (IdOfChildren, IdOfGuardian)
);

create index cf_client_guardian_child_idx on cf_client_guardian(IdOfChildren);
create index cf_client_guardian_guard_idx on cf_client_guardian(IdOfGuardian);

ALTER TABLE cf_orgs ADD COLUMN OrganizationType integer NOT NULL DEFAULT 0; -- по умолчнию все будут школами
update cf_orgs set OrganizationType=2 where RefectoryType=3;


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