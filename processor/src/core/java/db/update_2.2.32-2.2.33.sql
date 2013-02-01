--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.33
--! добавлено поле индексирование для подсчета сумм пополнений
CREATE INDEX cf_clientpayments_paysum_idx
  ON cf_clientpayments
  USING btree
  (paysum );

-- Добавлена секция OrgOwnerData в котором содержится список обслуживаемых организаций, при синхронизации внешних систем в качестве поставщиков с подсистемой координации и процессинга

-- Справочник базовых товаров
CREATE TABLE CF_Goods_BasicBasket
(
  IdOfBasicGood   bigserial              NOT NULL,
  Guid            character varying(36)  NOT NULL,
  CreatedDate     bigint                 NOT NULL,
  --! Значение даты последнего обновления при создании объекта равно дате из поля CreatedDate,
  --! используется в качестве версии базового товара
  LastUpdate      bigint                 NOT NULL,
  NameOfGood      character varying(512) NOT NULL,
  UnitsScale      integer                NOT NULL  DEFAULT 0,
  NetWeight       bigint                 NOT NULL,

  CONSTRAINT CF_Goods_BasicBasket_PK                  PRIMARY KEY (IdOfBasicGood),
  CONSTRAINT CF_Goods_BasicBasket_BasicGoodNumber_Key UNIQUE      (Guid),
  CONSTRAINT CF_Goods_BasicBasket_NameOfGood_Key      UNIQUE      (NameOfGood)

)
WITH (
  OIDS=FALSE
);

ALTER TABLE CF_Goods ADD COLUMN IdOfBasicGood bigint;
--! Дата последнего обновления базового товара на момент назначения ссылки на него товару поставщика
ALTER TABLE CF_Goods ADD COLUMN BasicGoodLastUpdate bigint;
ALTER TABLE CF_Goods ADD CONSTRAINT CF_Goods_IdOfBasicGood_FK FOREIGN KEY (IdOfBasicGood) REFERENCES CF_Goods_BasicBasket(IdOfBasicGood);

-- Добавлена секция OrgOwnerData в котором содержится список обслуживаемых организаций, при синхронизации внешних систем в качестве поставщиков с подсистемой координации и процессинга


-- CREATE OR REPLACE VIEW cf_contragents2cf_orgs AS
-- SELECT
--    c3_.IdOfContragent || '-' || org.IdOfOrg as Key,
--    sum(this_.paysum) AS sumbypaysum,
--    c3_.idofcontragent AS contragentid,
--    c3_.contragentname, org.idoforg AS organizationid
--    FROM cf_contragents c3_
--    LEFT JOIN cf_clientpayments this_ ON this_.idofcontragent = c3_.idofcontragent
--    LEFT JOIN cf_transactions tr1_ ON this_.idoftransaction = tr1_.idoftransaction
--    LEFT JOIN cf_clients cl2_ ON tr1_.idofclient = cl2_.idofclient
--    LEFT JOIN cf_orgs org ON cl2_.idoforg = org.idoforg
--   GROUP BY c3_.idofcontragent, org.idoforg;