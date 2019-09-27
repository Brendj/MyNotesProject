--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 194

alter table cf_preorder_menudetail
  add column idofgood bigint,
  add CONSTRAINT cf_preorder_menudetail_idofgood_fk FOREIGN KEY (idofgood)
    REFERENCES cf_goods (idofgood) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION;

--! ФИНАЛИЗИРОВАН 27.09.2019, НЕ МЕНЯТЬ