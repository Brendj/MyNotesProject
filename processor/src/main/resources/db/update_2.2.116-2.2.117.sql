--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.116

--Поле родственной связи между опекуном и ребенком текстом
ALTER TABLE cf_client_guardian ADD COLUMN relation integer;

--Поле с именем товара, который указан в записи сверки
ALTER TABLE cf_taloon_approval ADD COLUMN goodsname CHARACTER VARYING(512);
--Поле с сылкой на товар, который указан в записи сверки
ALTER TABLE cf_taloon_approval ADD COLUMN goodsguid CHARACTER VARYING(36);

ALTER TABLE CF_RegistryChange ADD COLUMN guardiansCount integer;

--! ФИНАЛИЗИРОВАН (Семенов, 150816) НЕ МЕНЯТЬ