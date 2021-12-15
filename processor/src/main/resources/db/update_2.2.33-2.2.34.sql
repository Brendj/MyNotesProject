--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.34
-- Добавлен расчет суммы базовой корзины
--! Добавлен справочник цен базовых товаров
CREATE TABLE Cf_Good_Basic_Basket_Price
(
  IdOfGoodBasicBasketPrice bigserial NOT NULL,
  IdOfBasicGood bigint NOT NULL,
  IdOfGood bigint NOT NULL,
  GlobalVersion bigint,
  GlobalVersionOnCreate bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean DEFAULT FALSE,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 1,
  OrgOwner bigint,
  Price bigint,
  CONSTRAINT Cf_Good_Basic_Basket_Price_PK                  PRIMARY KEY (IdOfGoodBasicBasketPrice),
  CONSTRAINT Cf_Good_Basic_Basket_Price_BasicGoodNumber_Key UNIQUE      (Guid)
);

--! ФИНАЛИЗИРОВАН (Кадыров, 130213) НЕ МЕНЯТЬ