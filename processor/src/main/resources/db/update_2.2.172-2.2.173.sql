--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.173

-- Флаг "Законный представитель"
alter table cf_client_guardian
  add column isLegalRepresent integer;

-- удаляем неиспользуемые в сверке ОО поля
alter table cf_orgregistrychange_item
  drop column interdistrictcouncil,
  drop column interdistrictcouncilfrom,
  drop column interdistrictcouncilchief,
  drop column interdistrictcouncilchieffrom,
  drop column mainbuilding,
  add column introductionqueue character varying(64),
  add column introductionqueuefrom character varying(64),
  add column director character varying(255),
  add column directorfrom character varying(255);

alter table cf_orgregistrychange
  drop column interdistrictcouncil,
  drop column interdistrictcouncilfrom,
  drop column interdistrictcouncilchief,
  drop column interdistrictcouncilchieffrom,
  drop column mainbuilding;

-- время последнего получения льготы от нси
alter table cf_client_dtiszn_discount_info
  add column lastreceiveddate bigint;

--! ФИНАЛИЗИРОВАН 17.01.2019, НЕ МЕНЯТЬ