--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.145

--таблица данных для сверки (загрузка из файла от НСИ)
create table cf_registry_file
(
  guidofclient character varying(40),
  guidoforg character varying(40),
  firstname character varying(64),
  secondname character varying(128),
  surname character varying(128),
  birthdate character varying(32),
  gender character varying(10),
  benefit character varying(200),
  parallel character varying(200),
  letter character varying(200),
  clazz character varying(200),
  currentclassorgroup character varying(200),
  status character varying(200),
  rep_firstname character varying(64),
  rep_secondname character varying(128),
  rep_surname character varying(128),
  rep_phone character varying(200),
  rep_who character varying(200),
  agegrouptype character varying(200)
);

--Убираем уникальность кода поставщика
alter table cf_card_signs drop constraint cf_card_signs_code_uk;

--! ФИНАЛИЗИРОВАН (Семенов, 171004) НЕ МЕНЯТЬ