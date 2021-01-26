/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 867

alter table cf_preorder_menudetail alter column groupname type character varying(256);

alter table cf_preorder_complex alter column complexname type character varying(256);