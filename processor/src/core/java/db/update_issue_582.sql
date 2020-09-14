/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE TABLE cf_mh_clientcard_ref
(
    idOfRef                 BIGSERIAL PRIMARY KEY,
    idofcard                BIGINT  NOT NULL REFERENCES cf_cards (idofcard),
    idofclient              BIGINT  NOT NULL REFERENCES cf_clients (idofclient),
    idOfRefInExternalSystem INTEGER,
    deleteState             BOOLEAN NOT NULL DEFAULT FALSE,
    isSend                  BOOLEAN NOT NULL DEFAULT FALSE,
    createDate              BIGINT  NOT NULL,
    lastUpdate              BIGINT  NOT NULL
);

COMMENT ON TABLE cf_mh_clientcard_ref IS 'Таблица свизей клиент<->карта в сторонней ИС МЭШ Контингент';
COMMENT ON COLUMN cf_mh_clientcard_ref.idOfRef IS 'ID записи';
COMMENT ON COLUMN cf_mh_clientcard_ref.idofcard IS 'Ссылка на карту';
COMMENT ON COLUMN cf_mh_clientcard_ref.idofclient IS 'Ссылка на клиента';
COMMENT ON COLUMN cf_mh_clientcard_ref.idOfRefInExternalSystem IS 'ID в нешней системе (МЭШ)';
COMMENT ON COLUMN cf_mh_clientcard_ref.isSend IS 'Флаг успешной отправки в МЭШ';
COMMENT ON COLUMN cf_mh_clientcard_ref.createDate IS 'Время создания записи';
COMMENT ON COLUMN cf_mh_clientcard_ref.lastUpdate IS 'Время последнего обновления';
COMMENT ON COLUMN cf_mh_clientcard_ref.deleteState IS 'Признак удаление связки';