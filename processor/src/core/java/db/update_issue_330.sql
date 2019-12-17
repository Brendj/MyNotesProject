--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 330

create table cf_preorder_flags
(
  idofpreorderflag bigserial NOT NULL PRIMARY KEY,
  idofclient bigint NOT NULL,
  informedspecialmenu integer,
  idofguardianspecialmenu bigint,
  allowedpreorder integer,
  idofguardianallowedpreorder bigint,
  createddate bigint,
  lastupdate bigint
);

insert into cf_preorder_flags(idofclient, informedspecialmenu, idofguardianspecialmenu, createddate)
  select c.idofclient, 1, cg.idofguardian, extract(epoch from now()) * 1000
  from cf_clients c join cf_client_guardian cg on c.idofclient = cg.idofchildren where cg.informedspecialmenu = 1;

--alter table cf_client_guardian drop column informedspecialmenu; ??

CREATE INDEX cf_preorder_flags_idofclient_idx
ON cf_preorder_flags
USING btree
(idofclient);

alter table cf_preorder_flags
  ADD CONSTRAINT cf_preorder_flags_fk_client FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT cf_preorder_flags_fk_specialmenu FOREIGN KEY (idofguardianspecialmenu)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT cf_preorder_flags_fk_allowedpreorder FOREIGN KEY (idofguardianallowedpreorder)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  ADD CONSTRAINT cf_preorder_flags_client_informedspecialmenu_key UNIQUE (idofclient, idofguardianspecialmenu);
