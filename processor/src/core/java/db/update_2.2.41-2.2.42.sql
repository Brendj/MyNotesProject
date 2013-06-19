--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.42
-- Таблица регистрации временных карт
-- CREATE TABLE cf_cards_temp (
--   IdOfCartTemp bigserial,
--   CardNo bigint NOT NULL,              --! номер карты
--   IdOfOrg bigint NOT NULL,             --! идентификатор организациии
--   CardPrintedNo character varying(24),   --! номер нанесенный на карту
--   Station int not null default 0,
--   CreateDate bigint notnull
--   CloseDate bigint
--
--   CONSTRAINT CardNo_Unique UNIQUE (CardNo)
-- );
--
-- CREATE TABLE cf_synchistory_exceptions
-- (
--   idofsynchistoryexception bigserial NOT NULL,
--   idoforg bigint NOT NULL,
--   idofsync bigint NOT NULL,
--   message character varying(512) NOT NULL,
--   CONSTRAINT cf_synchistory_exceptions_pk PRIMARY KEY (idofsynchistoryexception),
--   CONSTRAINT cf_synchistory_exceptions_organization FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg),
--   CONSTRAINT cf_synchistory_exceptions_sync FOREIGN KEY (idofsync) REFERENCES cf_synchistory (idofsync)
-- );


create or replace function generate_uuid_v4() returns VARCHAR(36) as '
declare value VARCHAR(36);
begin
  value = lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || `-`
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || `-`
  value = value || lpad((to_hex((ceil(random() * 255)::int & 15) | 64)), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || `-`
  value = value || lpad((to_hex((ceil(random() * 255)::int & 63) | 128)), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || `-`
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  value = value || lpad(to_hex(ceil(random() * 255)::int), 2, `0`);
  RETURN value::uuid;
end;' language 'plpgsql';

