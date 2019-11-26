--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 201

-- 203: Сущность настройкии расписания синхранизации для ОО
CREATE TABLE cf_syncsettings
(
    idofsyncsetting bigserial primary key,
    idoforg bigint NOT NULL references cf_orgs(idoforg),
    contenttype varchar(128) NOT NULL,
    everysecond integer,
    limitstarthour integer,
    limitendhour integer,
    monday boolean NOT NULL DEFAULT false,
    tuesday boolean NOT NULL DEFAULT false,
    wednesday boolean NOT NULL DEFAULT false,
    thursday boolean NOT NULL DEFAULT false,
    friday boolean NOT NULL DEFAULT false,
    saturday boolean NOT NULL DEFAULT false,
    sunday boolean NOT NULL DEFAULT false,
    version bigint NOT NULL DEFAULT 0,
    deletestate boolean NOT NULL DEFAULT false,
    createddate bigint NOT NULL DEFAULT (date_part(cast('epoch' as text), now()) * cast(1000 as double precision)),
    lastupdate bigint,
    concretetime varchar(256),

    CONSTRAINT cf_syncsettings_limitendhour_check CHECK (limitendhour >= 0 AND limitendhour <= 24),
    CONSTRAINT cf_syncsettings_limitstarthour_check CHECK (limitstarthour >= 0 AND limitstarthour <= 24),
    CONSTRAINT cf_syncsettings_everysecond_check CHECK (everysecond <> 0)
);

CREATE UNIQUE INDEX cf_syncsettings_idoforg_contenttype
    ON cf_syncsettings (idoforg, contenttype)
    WHERE deletestate IS FALSE;

alter table cf_visitreqresolutionhist add column extraId bigserial not null;

--! ФИНАЛИЗИРОВАН 26.11.2019, НЕ МЕНЯТЬ