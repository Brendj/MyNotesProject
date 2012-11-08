-- Table: cf_complexinfo_discountdetail

-- DROP TABLE cf_complexinfo_discountdetail;

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
  idofclientgroup bigint NOT NULL,
  maxcount integer NOT NULL,
  idoforg bigint NOT NULL,
  CONSTRAINT cf_complexinfo_discountdetail_pk PRIMARY KEY (idofdiscountdetail ),
  CONSTRAINT cf_complexinfo_discountdetail_idofclientgroup_fk FOREIGN KEY (idoforg, idofclientgroup)
      REFERENCES cf_clientgroups (idoforg, idofclientgroup) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_complexinfo_discountdetail_idoforg_fk FOREIGN KEY (idoforg)
      REFERENCES cf_orgs (idoforg) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_complexinfo_discountdetail
  OWNER TO postgres;

-- Column: usetrdiscount

-- ALTER TABLE cf_complexinfo DROP COLUMN usetrdiscount;
-- userdiscount 0 – торговые скидки не применяются, 1 – торговые скидки применяются (только для динамических комплексов)
ALTER TABLE cf_complexinfo ADD COLUMN usetrdiscount integer;
ALTER TABLE cf_complexinfo ALTER COLUMN usetrdiscount SET DEFAULT 0;

-- Column: idofdiscountdetail

-- ALTER TABLE cf_complexinfo DROP COLUMN idofdiscountdetail;
-- idofdiscountdetail ссылка на элемент торговой скидки
ALTER TABLE cf_complexinfo ADD COLUMN idofdiscountdetail bigint;

-- Column: idoforg

-- ALTER TABLE cf_complexinfo_discountdetail DROP COLUMN idoforg;
--idoforg id организации (нужен только для создания foreign key на cf_clientgroups)
ALTER TABLE cf_complexinfo_discountdetail ALTER COLUMN idoforg DROP NOT NULL;

-- Column: idofclientgroup

-- ALTER TABLE cf_complexinfo_discountdetail DROP COLUMN idofclientgroup;
--idofclientgroup id группы клиентов
ALTER TABLE cf_complexinfo_discountdetail ALTER COLUMN idofclientgroup DROP NOT NULL;

-- Column: maxcount

-- ALTER TABLE cf_complexinfo_discountdetail DROP COLUMN maxcount;

ALTER TABLE cf_complexinfo_discountdetail ALTER COLUMN maxcount DROP  NOT NULL;

-- Column: idofitem

-- ALTER TABLE cf_complexinfodetail DROP COLUMN idofitem;

ALTER TABLE cf_complexinfodetail ADD COLUMN idofitem bigint;

-- Column: idofmenudetail

-- ALTER TABLE cf_complexinfo DROP COLUMN idofmenudetail;

ALTER TABLE cf_complexinfo ADD COLUMN idofmenudetail bigint;

-- New FK to cf_menudetail
ALTER TABLE cf_complexinfo ADD CONSTRAINT cf_complexinfo_idofmenudetail_fk FOREIGN KEY (idofmenudetail)
      REFERENCES cf_menudetails (idofmenudetail) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
