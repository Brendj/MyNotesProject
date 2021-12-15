--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.142

--Таблица для настроек платного питания
create table cf_feeding_settings
(
  idofsetting bigserial not null,
  settingname character varying(128),
  limitamount bigint not null,
  lastupdate bigint,
  idofuser bigint not null,
  constraint cf_feeding_settings_pk primary key (idofsetting),
  CONSTRAINT cf_feeding_settings_idofuser_fk FOREIGN KEY (idofuser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--таблица связи Настройка платного питания - Организации
create table cf_feeding_settings_orgs
(
  idofsetting bigint not null,
  idoforg bigint not null,
  constraint cf_feeding_settings_orgs_pk primary key (idofsetting, idoforg),
  constraint cf_feeding_settings_orgs_idofsetting_fk FOREIGN KEY (idofsetting)
    REFERENCES cf_feeding_settings (idofsetting) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  constraint cf_feeding_settings_orgs_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  constraint cf_feeding_settings_UQ UNIQUE (idoforg)
);

--увеличиваем размерность для сохранения названий групп вместо идентификаторов
alter table cf_complex_schedules ALTER COLUMN groupsids SET DATA TYPE character varying(512);

--новое поле для количества заявок на временных клиентов
alter table cf_goods_requests_positions add column tempclientscount bigint;

--! ФИНАЛИЗИРОВАН (Семенов, 170923) НЕ МЕНЯТЬ