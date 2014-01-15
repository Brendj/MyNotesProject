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

