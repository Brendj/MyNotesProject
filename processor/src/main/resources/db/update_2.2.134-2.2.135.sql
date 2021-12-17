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

ALTER TABLE cf_transactions ADD COLUMN sendtoexternal INTEGER NOT NULL DEFAULT 1;
ALTER TABLE cf_orderdetails ADD COLUMN sendtoexternal INTEGER NOT NULL DEFAULT 1;

CREATE INDEX cf_transactions_sendtoexternal_partial_idx ON cf_transactions(sendtoexternal) where sendtoexternal = 0;
CREATE INDEX cf_orderdetails_sendtoexternal_partial_idx ON cf_orderdetails(sendtoexternal) where sendtoexternal = 0;

ALTER TABLE cf_orgs ADD COLUMN registryurl VARCHAR(256) DEFAULT '';

--! ФИНАЛИЗИРОВАН (Семенов, 310517) НЕ МЕНЯТЬ