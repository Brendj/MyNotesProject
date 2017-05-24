--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.135

--Таблица для хранения количественных показателей ОО
create table cf_org_inventory
  (
    idoforginventory bigserial NOT NULL,
    idoforg bigint NOT NULL,
    amount_armadmin INTEGER,
    amount_armcontroller INTEGER,
    amount_turnstiles INTEGER,
    amount_elocks INTEGER,
    amount_ereaders INTEGER,
    amount_infopanels INTEGER,
    amount_armoperator INTEGER,
    amount_infokiosks INTEGER,
    amount_armlibrary INTEGER,
    CONSTRAINT cf_org_inventory_pkey PRIMARY KEY (idoforginventory),
    CONSTRAINT cf_org_inventory_idoforg_fk FOREIGN KEY (idoforg)
    REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
  );

CREATE INDEX cf_org_inventory_idoforg_idx ON cf_org_inventory USING btree (idoforg);

