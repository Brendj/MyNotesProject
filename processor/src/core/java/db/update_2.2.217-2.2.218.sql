--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 218

-- 499: добавление новых полей в реестр талонов

ALTER TABLE cf_taloon_approval
    ADD COLUMN complexid bigint,
    ADD COLUMN bywebsupplier boolean NOT NULL DEFAULT false;


-- 499: добавление нового поля в таблицу детализации комплексов предзаказа

ALTER TABLE cf_preorder_menudetail
    ADD COLUMN idofdish bigint;


-- 499: добавление новых полей в реестр талонов

ALTER TABLE cf_taloon_preorder
    ADD COLUMN idofdish bigint,
    ADD COLUMN bywebsupplier boolean NOT NULL DEFAULT false;
