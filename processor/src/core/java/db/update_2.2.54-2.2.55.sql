--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.55

CREATE TABLE cf_client_guardian
(
  idofclientguardian bigserial NOT NULL,
  idofchildren bigint NOT NULL,
  idofguardian bigint NOT NULL,
  CONSTRAINT cf_client_guardian_pk PRIMARY KEY (idofclientguardian),
  CONSTRAINT cf_client_guardian_idofchildren_fk FOREIGN KEY (idofchildren) REFERENCES cf_clients (idofclient),
  CONSTRAINT cf_client_guardian_idofguardian_fk FOREIGN KEY (idofguardian) REFERENCES cf_clients (idofclient)
);

create index cf_client_guardian_child_idx on cf_client_guardian(idofchildren);
create index cf_client_guardian_guard_idx on cf_client_guardian(idofguardian);

alter table cf_goods_requests_positions add column LastDailySampleCount bigint default null;
alter table cf_goods_requests_positions add column LastTotalCount bigint default null;

alter table cf_goods_requests_positions drop column UpdateHistory;
