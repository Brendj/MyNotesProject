--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.132

alter table cf_clientallocationrule alter column groupfilter type character varying(1024);

--Таблица для хранения информационных сообщений в АРМ администратора
create table cf_info_messages
(
  idOfInfoMessage bigint not null,
  header character varying(128) not null,
  content text not null,
  createdDate bigint not null,
  version bigint,
  idOfUser bigint not null,
  CONSTRAINT cf_info_messages_pkey PRIMARY KEY (idofinfomessage),
  CONSTRAINT cf_info_message_details_idofuser_fk FOREIGN KEY (idOfUser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Таблица детализации информационных сообщений по организациям
create table cf_info_message_details
(
  IdOfInfoMessage bigint not null,
  IdOfOrg bigint not null,
  SendDate bigint,
  CONSTRAINT cf_info_message_details_pk PRIMARY KEY (IdOfInfoMessage, IdOfOrg),
  CONSTRAINT cf_info_message_details_idoforg_fk FOREIGN KEY (idOfOrg)
    REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_info_message_details_idofinfomessage_fk FOREIGN KEY (idOfInfoMessage)
  REFERENCES cf_info_messages (idofinfomessage) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--генератор для ключа таблицы сообщений
alter table CF_Generators add column IdOfInfoMessage bigint not null default 0;

--! ФИНАЛИЗИРОВАН (Семенов, 100417) НЕ МЕНЯТЬ