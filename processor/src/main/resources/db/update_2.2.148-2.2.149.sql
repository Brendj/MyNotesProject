--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.149

--Таблица связей базовый товар - меню (цена, наименование товара + производственная конфигурация из меню)
CREATE TABLE cf_good_bb_menu_price
(
  idofgoodbbmenuprice bigserial NOT NULL,
  idofbasicgood bigint NOT NULL,
  idofconfigurationprovider bigint NOT NULL,
  menudate bigint NOT NULL ,
  price bigint,
  menudetailname character varying(512) NOT NULL,
  CONSTRAINT cf_good_bb_menu_price_pk PRIMARY KEY (idofgoodbbmenuprice),
  CONSTRAINT cf_good_bb_menu_price_unique UNIQUE (idofbasicgood, idofconfigurationprovider, menudate, menudetailname)
)
WITH (
OIDS=FALSE
);

--! ФИНАЛИЗИРОВАН (Семенов, 171128) НЕ МЕНЯТЬ
