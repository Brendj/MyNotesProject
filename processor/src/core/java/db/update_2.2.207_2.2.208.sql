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