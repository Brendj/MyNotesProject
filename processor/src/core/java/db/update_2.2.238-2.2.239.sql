--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 239
COMMENT ON COLUMN cf_externalevents.evtstatus IS '0 - вход (в Музей или в Минкультуру), 1 - выход (из Музея или Минкультуры), 2 - Рекомендация об освобождении, 3 - Отмена рекомендации об освобождении, 4 - Рекомендация о возможности посещать ОО, 5 - Отмена рекомендации о возможности посещать ОО';

--748
alter table cf_wt_complexes_items drop column count_dishes;

--582, 727
CREATE TABLE cf_mh_clientcard_ref
(
    idofcard                BIGINT PRIMARY KEY REFERENCES cf_cards (idofcard),
    idofclient              BIGINT NOT NULL REFERENCES cf_clients (idofclient),
    idOfRefInExternalSystem INTEGER,
    isSend                  BOOLEAN NOT NULL DEFAULT FALSE,
    createDate              BIGINT  NOT NULL,
    lastUpdate              BIGINT  NOT NULL
);

COMMENT ON TABLE cf_mh_clientcard_ref IS 'Таблица свизей клиент<->карта в сторонней ИС МЭШ Контингент';
COMMENT ON COLUMN cf_mh_clientcard_ref.idofcard IS 'Ссылка на карту';
COMMENT ON COLUMN cf_mh_clientcard_ref.idofclient IS 'Ссылка на клиента';
COMMENT ON COLUMN cf_mh_clientcard_ref.idOfRefInExternalSystem IS 'ID в нешней системе (МЭШ)';
COMMENT ON COLUMN cf_mh_clientcard_ref.isSend IS 'Флаг успешной отправки в МЭШ';
COMMENT ON COLUMN cf_mh_clientcard_ref.createDate IS 'Время создания записи';
COMMENT ON COLUMN cf_mh_clientcard_ref.lastUpdate IS 'Время последнего обновления';

--! ФИНАЛИЗИРОВАН 29.10.2020, НЕ МЕНЯТЬ