--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 559

alter table cf_applications_for_food
  add column discountdatestart bigint,
  add column discountdateend bigint;

COMMENT ON COLUMN cf_applications_for_food.discountdatestart is 'Дата начала действия льготы';
COMMENT ON COLUMN cf_applications_for_food.discountdateend is 'Дата окончания действия льготы';