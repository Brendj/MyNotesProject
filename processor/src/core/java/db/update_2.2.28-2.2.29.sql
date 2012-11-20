-- create or replace function add_column(_tbl regclass, _col text, _type regtype, _params text) returns bool as '
-- declare success bool;
-- begin
-- success := true;
-- if exists (
--     SELECT 1 FROM pg_attribute
--     WHERE attrelid = _tbl
--     AND attname = _col
--     AND NOT attisdropped)
-- then
--     RAISE NOTICE ''Column % already exists in %.'', _col, _tbl;
-- else
--     EXECUTE ''ALTER TABLE '' || _tbl || '' ADD COLUMN '' || quote_ident(_col) || '' '' || _type || '' '' || _params;
-- end if;
-- return success;
-- end;' language 'plpgsql';

CREATE TABLE IF NOT EXISTS cf_projectstate_data
  (
  Period bigint NOT NULL,
  Type int NOT NULL,
  StringKey character varying(128),
  StringValue character varying(128),
  generationdate bigint NOT NULL,
  CONSTRAINT cf_projectstate_data_pk PRIMARY KEY (Period, Type, StringKey)
  );

-- Добавление колонки currentprice к таблице cf_complexinfo
ALTER TABLE cf_complexinfo ADD COLUMN currentprice bigint;
--SELECT add_column('cf_complexinfo', 'currentprice', 'bigint', '');

-- Добавление колонки count к таблице cf_complexinfodetail
ALTER TABLE cf_complexinfodetail ADD COLUMN count integer;
--SELECT add_column('cf_complexinfodetail', 'count', 'integer', '');

-- Добавленна свяска к документу
ALTER TABLE cf_internal_disposing_document_positions ADD COLUMN IdOfGood bigint NOT NULL;
ALTER TABLE cf_internal_disposing_document_positions ADD CONSTRAINT cf_internal_disposing_document_positions_fk_good FOREIGN KEY (IdOfGood) REFERENCES cf_goods (IdOfGood);