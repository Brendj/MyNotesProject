-- Изменение таблицы соответсвия оборудования школам
alter table cf_org_accessories add column IdOfAccessory BIGINT NOT NULL;
alter table cf_org_accessories rename column ACCESSORY_TYPE to AccessoryType;
alter table cf_org_accessories rename column ACCESSORY_NUMBER to AccessoryNumber;
alter table cf_org_accessories alter column AccessoryNumber type VARCHAR(40);
alter table cf_org_accessories drop constraint cf_org_accessories_pk;
alter table cf_org_accessories add constraint cf_org_accessories_pk PRIMARY KEY (IdOfAccessory);
alter table cf_org_accessories add CONSTRAINT cf_org_accessories_unique UNIQUE (IdOfAccessory, IdOfSourceOrg, IdOfTargetOrg, AccessoryType, AccessoryNumber);
-- Добавление генератора идентифиактора для оборудования корпусов
ALTER TABLE CF_Generators ADD COLUMN IdOfAccessory BIGINT NOT NULL DEFAULT 0;