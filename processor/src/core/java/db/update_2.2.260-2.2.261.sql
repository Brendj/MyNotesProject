-- Пакет обновлений 261

-- Индекс для guid в таблице EMIAS
CREATE INDEX cf_emias_guid_idx ON cf_emias USING btree (guid);
