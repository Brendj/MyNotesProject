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

-- Добавление колонки count к таблице cf_complexinfodetail
ALTER TABLE cf_complexinfodetail ADD COLUMN count integer;

-- Добавленна свяска к документу
-- ALTER TABLE cf_internal_disposing_document_positions ADD COLUMN IdOfGood bigint NOT NULL;
-- ALTER TABLE cf_internal_disposing_document_positions ADD CONSTRAINT cf_internal_disposing_document_positions_fk_good FOREIGN KEY (IdOfGood) REFERENCES cf_goods (IdOfGood);

--! ФИНАЛИЗИРОВАН (Кадыров, 121123) НЕ МЕНЯТЬ