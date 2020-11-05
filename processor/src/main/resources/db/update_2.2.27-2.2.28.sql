--! create or replace function add_column(_tbl regclass, _col text, _type regtype, _params text) returns bool as '
--! declare success bool;
--! begin
--! success := true;
--! if exists (
--!     SELECT 1 FROM pg_attribute
--!     WHERE attrelid = _tbl
--!     AND attname = _col
--!     AND NOT attisdropped)
--! then
--!     RAISE NOTICE ''Column % already exists in %.'', _col, _tbl;
--! else
--!     EXECUTE ''ALTER TABLE '' || _tbl || '' ADD COLUMN '' || quote_ident(_col) || '' '' || _type || '' '' || _params;
--! end if;
--! return success;
--! end;' language 'plpgsql';

-- cf_complexinfo_discountdetail
--торговая скидка
--idofdiscountdetail id
--size размер скидки
--isallgroups 0 – применяется для конкретной группы, 1 – применяются для всех групп
--idofclientgroup id группы клиентов
--maxcount Максимальное количество применений скидки: -1 – не ограничено,--  >=0 указанное число раз
--idoforg id организации (нужен только для создания foreign key на cf_clientgroups)
CREATE TABLE cf_complexinfo_discountdetail
(
  idofdiscountdetail bigserial NOT NULL,
  size double precision NOT NULL,
  isallgroups integer NOT NULL,
  idofclientgroup bigint,
  maxcount integer,
  idoforg bigint,
  CONSTRAINT cf_complexinfo_discountdetail_pk PRIMARY KEY (idofdiscountdetail ),
  CONSTRAINT cf_complexinfo_discountdetail_idofclientgroup_fk FOREIGN KEY (idoforg, idofclientgroup) REFERENCES cf_clientgroups (idoforg, idofclientgroup),
  CONSTRAINT cf_complexinfo_discountdetail_idoforg_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg)
);

--! ALTER TABLE cf_complexinfo DROP COLUMN usetrdiscount;
-- userdiscount 0 – торговые скидки не применяются, 1 – торговые скидки применяются (только для динамических комплексов)
--! SELECT add_column('cf_complexinfo', 'usetrdiscount', 'integer', 'DEFAULT 0');
ALTER TABLE cf_complexinfo ADD COLUMN usetrdiscount integer  DEFAULT 0;
--!ALTER TABLE cf_complexinfo ALTER COLUMN usetrdiscount SET DEFAULT 0;

-- Column: idofdiscountdetail

--! ALTER TABLE cf_complexinfo DROP COLUMN idofdiscountdetail;
--! idofdiscountdetail ссылка на элемент торговой скидки
-- Добавлена ссылка на элемент торговой скидки
--! SELECT add_column('cf_complexinfo', 'idofdiscountdetail', 'bigint', '');
ALTER TABLE cf_complexinfo ADD COLUMN idofdiscountdetail bigint;

--! ALTER TABLE cf_complexinfodetail DROP COLUMN idofitem;
-- SELECT add_column('cf_complexinfodetail', 'idofitem', 'bigint', '');
ALTER TABLE cf_complexinfodetail ADD COLUMN idofitem bigint;

-- Column: idofmenudetail

--! ALTER TABLE cf_complexinfo DROP COLUMN idofmenudetail;
-- SELECT add_column('cf_complexinfo', 'idofmenudetail', 'bigint', '');
ALTER TABLE cf_complexinfo ADD COLUMN idofmenudetail bigint;

-- New FK to cf_menudetail
--! ALTER TABLE cf_complexinfo DROP CONSTRAINT cf_complexinfo_idofmenudetail_fk;
ALTER TABLE cf_complexinfo ADD CONSTRAINT cf_complexinfo_idofmenudetail_fk FOREIGN KEY (idofmenudetail) REFERENCES cf_menudetails (idofmenudetail);

-- Changes to cf_menudetails
ALTER TABLE cf_menudetails ALTER COLUMN menudetailoutput DROP NOT NULL;
ALTER TABLE cf_menudetails ALTER COLUMN price DROP NOT NULL;
--! SELECT add_column('cf_menudetails', 'flags', 'integer', 'NOT NULL DEFAULT 1');
ALTER TABLE cf_menudetails ADD COLUMN flags integer NOT NULL DEFAULT 1;
--! SELECT add_column('cf_menudetails', 'priority', 'integer', 'DEFAULT 0');
ALTER TABLE cf_menudetails ADD COLUMN priority integer DEFAULT 0;

-- Настройки ECafe администратора
CREATE TABLE cf_ECafeSettings
(
  IdOfECafeSetting bigserial NOT NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  SettingValue character varying(128),
  Identificator bigint,
  CONSTRAINT cf_ECafeSetting_pk PRIMARY KEY (IdOfECafeSetting)
);

-- Увиличена размерность полей меню
ALTER TABLE cf_menudetails ALTER COLUMN menupath SET DATA TYPE character varying(512);
ALTER TABLE cf_menudetails ALTER COLUMN menudetailname SET DATA TYPE character varying(512);
ALTER TABLE cf_menuexchange ALTER COLUMN menudata SET DATA TYPE character varying(52650);

-- Хитрое ограничение уникальности на таблицу комплексных скидок
-- Index: cf_complexinfo_discountdetail_2col_uni_idx

DROP INDEX IF EXISTS cf_complexinfo_discountdetail_2col_uni_idx;
CREATE UNIQUE INDEX cf_complexinfo_discountdetail_2col_uni_idx
  ON cf_complexinfo_discountdetail
  USING btree
  (size , isallgroups )
  WHERE maxcount IS NULL AND idofclientgroup IS NULL AND idoforg IS NULL;

-- Index: cf_complexinfo_discountdetail_3col_uni_idx

DROP INDEX IF EXISTS cf_complexinfo_discountdetail_3col_uni_idx;
CREATE UNIQUE INDEX cf_complexinfo_discountdetail_3col_uni_idx
  ON cf_complexinfo_discountdetail
  USING btree
  (size , isallgroups , maxcount )
  WHERE maxcount IS NOT NULL AND idofclientgroup IS NULL AND idoforg IS NULL;

-- Index: cf_complexinfo_discountdetail_5col_uni_idx

DROP INDEX IF EXISTS cf_complexinfo_discountdetail_5col_uni_idx;
CREATE UNIQUE INDEX cf_complexinfo_discountdetail_5col_uni_idx
  ON cf_complexinfo_discountdetail
  USING btree
  (size , isallgroups , maxcount , idofclientgroup , idoforg )
  WHERE maxcount IS NOT NULL AND idofclientgroup IS NOT NULL AND idoforg IS NOT NULL;

--! ФИНАЛИЗИРОВАН (Кадыров, 121113) НЕ МЕНЯТЬ

--! Если уже были удалены внешние ключи из таблицы cf_complexinfo_discountdetail

--! ALTER TABLE cf_complexinfo_discountdetail DROP CONSTRAINT IF EXISTS cf_complexinfo_discountdetail_idoforg_fk;
--! ALTER TABLE cf_complexinfo_discountdetail ADD CONSTRAINT cf_complexinfo_discountdetail_idoforg_fk FOREIGN KEY (idoforg)
--! REFERENCES cf_orgs (idoforg) MATCH SIMPLE
--! ON UPDATE NO ACTION ON DELETE NO ACTION

--! Требуется изменить сопоставление для составного внешнего ключа (MATCH SIMPLE на MATCH FULL)

--! ALTER TABLE cf_complexinfo_discountdetail DROP CONSTRAINT IF EXISTS cf_complexinfo_discountdetail_idofclientgroup_fk;
--! ALTER TABLE cf_complexinfo_discountdetail ADD CONSTRAINT cf_complexinfo_discountdetail_idofclientgroup_fk
--! FOREIGN KEY (idoforg, idofclientgroup)
--! REFERENCES cf_clientgroups (idoforg, idofclientgroup) MATCH FULL
--! ON UPDATE NO ACTION ON DELETE NO ACTION,
