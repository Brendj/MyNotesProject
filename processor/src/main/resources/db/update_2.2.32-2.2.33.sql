--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.33

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

);

ALTER TABLE CF_Goods ADD COLUMN IdOfBasicGood bigint;
--! Дата последнего обновления базового товара на момент назначения ссылки на него товару поставщика
ALTER TABLE CF_Goods ADD COLUMN BasicGoodLastUpdate bigint;
ALTER TABLE CF_Goods ADD CONSTRAINT CF_Goods_IdOfBasicGood_FK FOREIGN KEY (IdOfBasicGood) REFERENCES CF_Goods_BasicBasket(IdOfBasicGood);

-- Добавлена секция OrgOwnerData в котором содержится список обслуживаемых организаций, при синхронизации внешних систем в качестве поставщиков с подсистемой координации и процессинга

-- Добавляем поля ролей для пользователей
ALTER TABLE CF_Users ADD COLUMN IdOfRole bigint NOT NULL DEFAULT 0;
ALTER TABLE CF_Users ADD COLUMN RoleName character varying(128);
--
-- --! Задаем имена по умолчаеию
UPDATE cf_users SET RoleName=UserName;



--! Добавление колонки региона для project state, чтобы было удобнее анализировать таблицу
alter table CF_ProjectState_Data add region char(128) default 'Все';
--! Добавление колонки комментариев для project state, чтобы было удобнее анализировать таблицу
alter table CF_ProjectState_Data add comments char(255) default '';
--! Обновление ключа - добавляем в него регион
alter table CF_ProjectState_Data drop constraint cf_projectstate_data_pk;
alter table CF_ProjectState_Data add constraint cf_projectstate_data_pk primary key (Period, Type, Region, StringKey);

--! ФИНАЛИЗИРОВАН (Кадыров, 130212) НЕ МЕНЯТЬ