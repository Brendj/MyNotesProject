
-- Убираем ограничение уникальности
alter table cf_wt_dishes drop constraint cf_wt_dishes_code_contragent;

alter table cf_wt_dishes alter column code set data type varchar (32);