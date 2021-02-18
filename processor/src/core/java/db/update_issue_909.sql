--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 909

create index cf_wt_dish_categoryitem_relationships_idofdish_idx ON cf_wt_dish_categoryitem_relationships USING btree (idofdish);
create index cf_wt_dish_categoryitem_relationships_idofcategoryitem_idx ON cf_wt_dish_categoryitem_relationships USING btree (idofcategoryitem);

create index cf_wt_dish_groupitem_relationships_idofdish_idx ON cf_wt_dish_groupitem_relationships USING btree (idofdish);
create index cf_wt_dish_groupitem_relationships_idofgroupitem_idx ON cf_wt_dish_groupitem_relationships USING btree (idofgroupitem);

--! ФИНАЛИЗИРОВАН 05.02.2021, НЕ МЕНЯТЬ