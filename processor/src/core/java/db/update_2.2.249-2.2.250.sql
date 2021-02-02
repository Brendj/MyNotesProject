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

--! ФИНАЛИЗИРОВАН 02.02.2021, НЕ МЕНЯТЬ