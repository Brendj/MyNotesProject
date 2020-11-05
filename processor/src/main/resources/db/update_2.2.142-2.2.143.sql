--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.143

--флаг требования верификации при регистрации карты
alter table cf_orgs add column needVerifyCardSign integer not null default 0;

--таблица цифровых подписей
create table cf_card_signs
(
  idofcardsign serial not null,
  signtype integer not null,
  signdata bytea not null,
  manufacturercode integer NOT NULL ,
  manufacturername character varying(200) NOT NULL ,
  constraint cf_card_signs_pk primary key (idofcardsign),
  constraint cf_card_signs_code_uk unique (manufacturercode)
);

--номер сертификата (номер ключа) в таблице карт со ссылкой на таблицу цифровых подписей
alter table cf_cards
  add column cardSignCertNum integer,
  add constraint cf_cards_certnum_fk foreign key(cardSignCertNum)
  references cf_card_signs(idofcardsign) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

--новое поле для количества заявок на временных клиентов
alter table cf_goods_requests_positions add column lasttempclientscount bigint;

--! ФИНАЛИЗИРОВАН (Семенов, 171004) НЕ МЕНЯТЬ