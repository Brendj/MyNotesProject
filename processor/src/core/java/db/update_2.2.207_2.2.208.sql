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
