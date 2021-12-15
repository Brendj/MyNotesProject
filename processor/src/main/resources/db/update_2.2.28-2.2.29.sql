CREATE TABLE cf_projectstate_data
(
  Period bigint NOT NULL,
  Type int NOT NULL,
  StringKey character varying(128),
  StringValue character varying(128),
  generationdate bigint NOT NULL,
  CONSTRAINT cf_projectstate_data_pk PRIMARY KEY (Period, Type, StringKey)
);

-- Добавление колонки currentprice к таблице cf_complexinfo
ALTER TABLE CF_ComplexInfo ADD COLUMN CurrentPrice bigint;

-- Добавление колонки count к таблице cf_complexinfodetail
ALTER TABLE CF_ComplexInfoDetail ADD COLUMN Count integer;

-- Добавленна свяска к документу
ALTER TABLE CF_Internal_Disposing_Document_Positions ADD COLUMN IdOfGood bigint NOT NULL;
ALTER TABLE CF_Internal_Disposing_Document_Positions ADD CONSTRAINT cf_internal_disposing_document_positions_fk_good FOREIGN KEY (IdOfGood) REFERENCES CF_Goods (IdOfGood);

--! ФИНАЛИЗИРОВАН (Кадыров, 121123) НЕ МЕНЯТЬ