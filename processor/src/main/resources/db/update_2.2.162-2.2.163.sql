--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.163

-- ограничение на уникальность
CREATE UNIQUE INDEX cf_preorder_complex_idofclient_preorderdate_armcomplexid_unique ON cf_preorder_complex (idofclient, preorderdate, armcomplexid) WHERE deletedState = 0;
CREATE UNIQUE INDEX cf_preorder_menudetail_client_preorderdate_complex_menu_unique ON cf_preorder_menudetail (idofclient, preorderdate, idofpreordercomplex, armidofmenu) WHERE deletedState = 0;

--индекс по номеру карты
CREATE INDEX cf_cards_cardno_idx ON cf_cards USING btree (cardno);

alter table cf_preorder_menudetail add CONSTRAINT cf_preorder_menudetail_idofpreordercomplex_fk FOREIGN KEY (idofpreordercomplex)
  REFERENCES cf_preorder_complex (idofpreordercomplex) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

alter table cf_preorder_complex add column idOfOrgOnCreate bigint,
  add CONSTRAINT cf_preorder_complex_idoforgoncreate_fk FOREIGN KEY (idoforgoncreate)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX cf_preorder_complex_idoforgoncreate_idx ON cf_preorder_complex (idoforgoncreate);

alter table cf_preorder_menudetail alter column menudetailprice set not null;

--! ФИНАЛИЗИРОВАН 18.09.2018, НЕ МЕНЯТЬ