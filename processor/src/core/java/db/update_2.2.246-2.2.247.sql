/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 247

--Для работы 23-его типа уведомлений
CREATE SEQUENCE public.cf_cancel_preorder_notifications_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_cancel_preorder_notifications (
    ifofcancelpreordernotifications    int8 DEFAULT nextval('cf_cancel_preorder_notifications_id') NOT NULL,
    idofclient int8 NULL,
    typename varchar NULL,
    preorderdate int8 NULL,
    textmessage varchar NULL
);

--! ФИНАЛИЗИРОВАН 14.01.2020, НЕ МЕНЯТЬ