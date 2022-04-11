
-- Пакет обновлений issue 755
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(10025, cast(cast(extract(epoch from now()) * 1000 as bigint) as varchar));