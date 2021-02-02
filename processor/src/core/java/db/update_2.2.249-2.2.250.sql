/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Новой сущности для хранения новой истории по изменению льготы (#874)
CREATE TABLE cf_client_discount_history
(
    idOfClientDiscountHistory BIGSERIAL PRIMARY KEY,
    operationType             INTEGER      NOT NULL,
    registryDate              BIGINT       NOT NULL,
    idOfClient                BIGINT       NOT NULL REFERENCES cf_clients (idofclient),
    idOfCategoryDiscount      BIGINT       NOT NULL REFERENCES cf_categorydiscounts (idofcategorydiscount),
    comment                   VARCHAR(128) NOT NULL
);


COMMENT ON TABLE cf_client_discount_history IS 'Таблица новой истории изменений льгот клиента';
COMMENT ON COLUMN cf_client_discount_history.idOfClientDiscountHistory IS 'ID записи';
COMMENT ON COLUMN cf_client_discount_history.operationType IS 'Тип операции (0 - Создание, 1 - Изменение, 2 - Удаление)';
COMMENT ON COLUMN cf_client_discount_history.registryDate IS 'Дата регистрации записи';
COMMENT ON COLUMN cf_client_discount_history.idOfClient IS 'Ссылка на клиента, у которого изменилась льгота';
COMMENT ON COLUMN cf_client_discount_history.idOfCategoryDiscount IS 'Ссылка на измененную льготу';
COMMENT ON COLUMN cf_client_discount_history.comment IS 'Комментарий';

-- Пакет обновлений issue 889

--Создаем отдельные сиквенсы для каждой записи из таблицы cf_do_versions и инициализируем сиквенсы значениями версий из этой таблицы

CREATE OR REPLACE FUNCTION create_do_versions_sequences() RETURNS INTEGER
    LANGUAGE 'plpgsql' AS '
    DECLARE
        cname varchar;
    query_str varchar;
    seq_name varchar;
BEGIN

    for cname in select distributedobjectclassname FROM cf_do_versions
    loop
    seq_name = ''DO_VERSION_'' || cname || ''_seq'';
    query_str = ''create sequence '' || seq_name;
    execute query_str;

    query_str = E''select setval(\'''' || seq_name || E''\'', (select coalesce(max(currentversion), 0) + 1 from cf_do_versions where upper(distributedobjectclassname) = \'''' || upper(cname) || E''\'')) '';
    execute query_str;
    end loop;

    return 0;
    END ';

select create_do_versions_sequences();

--! ФИНАЛИЗИРОВАН 02.02.2021, НЕ МЕНЯТЬ