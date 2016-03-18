--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.112

--Увеличение размерности поля для условия выборки в отчетах по расписанию
ALTER TABLE cf_ruleconditions ALTER  conditionconstant TYPE character varying (50000);

--Новое поле для краткого наименование организации для поставщика в сверке организаций
ALTER TABLE cf_orgregistrychange_item ADD COLUMN shortnamesupplierfrom character varying(128);

--Новые поля ручного реестра талонов
ALTER TABLE cf_taloon_approval RENAME COLUMN qty to soldedqty, ADD COLUMN requestedqty integer,
  ADD COLUMN shippedqty integer, ADD COLUMN ispp_state integer NOT NULL DEFAULT 0, ADD COLUMN pp_state integer NOT NULL DEFAULT 0;

--Связь базовой корзины с детализацией меню
ALTER TABLE cf_good_basic_basket_price ADD COLUMN idofmenudetail bigint;

--внешний ключ на таблицу menudetails
ALTER TABLE cf_good_basic_basket_price ADD CONSTRAINT cf_good_basic_basket_price_menudetail_fk FOREIGN KEY (idofmenudetail)
REFERENCES cf_menudetails (idofmenudetail) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE cf_interactive_report_data (
  idofrecord bigint,
  idoforg bigint,
  value varchar (255),

  CONSTRAINT cf_interactivereport_cf_orgs_pk PRIMARY KEY (idoforg, idofrecord)
);