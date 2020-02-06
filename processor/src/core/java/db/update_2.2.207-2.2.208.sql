--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 208

-- флаг "Использовать Web-АРМ"
ALTER TABLE cf_orgs ADD COLUMN useWebArm BOOLEAN NOT NULL DEFAULT FALSE;

-- Таблица журнала отправки событий в Геопланер
CREATE TABLE cf_geoplaner_notifications_journal
(
    idofnotification BIGSERIAL PRIMARY KEY,
    idofclient BIGINT REFERENCES cf_clients(idofclient),
    idoforg BIGINT REFERENCES cf_orgs(idoforg),
    idofenterevents BIGINT,
    idoforder BIGINT,
    idofclientpayment BIGINT REFERENCES cf_clientpayments(idofclientpayment),
    eventtype INTEGER NOT NULL,
    response INTEGER,
    issend BOOLEAN NOT NULL DEFAULT FALSE,
    createdate BIGINT NOT NULL,
    errortext TEXT,
    nodename VARCHAR(32)
);

-- Сделать поле Пол обязательным. Всем "бесполым" клиентам по умолчанию устанавливается 1
UPDATE cf_registry
SET clientregistryversion = (SELECT max(clientregistryversion) FROM cf_registry) + 1
WHERE idofregistry = 1;

UPDATE cf_clients
SET gender                = 1,
    clientregistryversion = (SELECT max(clientregistryversion) FROM cf_registry)
WHERE gender IS NULL;

ALTER TABLE cf_clients
    ALTER gender SET DEFAULT 1,
    ALTER gender SET NOT NULL;

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

--! ФИНАЛИЗИРОВАН 05.02.2020, НЕ МЕНЯТЬ