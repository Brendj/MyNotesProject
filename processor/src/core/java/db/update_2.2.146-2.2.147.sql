--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.147

--флаг шестидневной недели по группам
alter table cf_groupnames_to_orgs add column issixdaysworkweek integer;

--таблица связок Базовый Товар - Производственная Конфигурация
create table cf_basicbasketgood_provider
(
  idofbasicbasketgoodprovider bigserial NOT NULL,
  idofbasicgood bigint NOT NULL,
  idofconfigurationprovider bigint NOT NULL,
  CONSTRAINT pk_basicbasketgood_provider PRIMARY KEY (idofbasicbasketgoodprovider),
  CONSTRAINT cf_basicbasketgood_provider_idofbasicgood_fk FOREIGN KEY (idofbasicgood)
  REFERENCES cf_goods_basicbasket (idofbasicgood) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_basicbasketgood_provider_idofconfigurationprovider_fk FOREIGN KEY (idofconfigurationprovider)
  REFERENCES cf_provider_configurations (idofconfigurationprovider) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Убираем уникальность по названию товара
alter table cf_goods_basicbasket drop constraint cf_goods_basicbasket_nameofgood_key;