/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 266

-- Функция блокировки активных карт с просроченным сроком действия
CREATE OR REPLACE FUNCTION block_active_card_with_overdue_valid_time() RETURNS integer
    LANGUAGE 'plpgsql' as
'
    DECLARE
        query RECORD;
    cnt   integer;
BEGIN
    cnt = 0;
    FOR query IN SELECT idOfCard, idOfClient, cardno FROM cf_cards WHERE state = 0 AND validdate < (extract(epoch from now()) * 1000) and idOfClient is not null
    LOOP
    UPDATE cf_cards
    SET state      = 6,
        lockreason = ''Заблокировано на сервере: истек срок действия карты'',
        lastupdate = (extract(epoch from now()) * 1000)
    WHERE idOfCard = query.idOfCard;
    INSERT INTO cf_history_card (idofcard, updatetime, formerowner, newowner, informationaboutcard, idofuser, idoftransaction)
    VALUES (query.idofcard, (extract(epoch from now()) * 1000), query.idofclient, query.idofclient, ''Блокировка карты №: '' || query.cardno || ''. Причина: Заблокировано на сервере: истек срок действия карты'', null, null);
    cnt = cnt + 1;
    END LOOP;
    RETURN cnt;
    END
';


alter table cf_mh_entity_changes
    add column uid varchar(36);

create table cf_mh_classes
(
    id               bigserial primary key,
    uid              varchar(36)  not null unique,
    organizationid   bigint,
    name             varchar(128) not null,
    parallelId       integer,
    educationStageId integer,
    createdate       timestamp    not null,
    lastupdate       timestamp    not null
);

alter table cf_mh_persons
    add column idofclass BIGINT references cf_mh_classes (id) on update no action on delete set null;

alter table cf_info_messages add column mtype integer not null default 0;
comment on column cf_info_messages.mtype is 'Тип сообщения: 0 - для толстого арма, 1 - для веб арма';



--! ФИНАЛИЗИРОВАН 28.07.2021, НЕ МЕНЯТЬ