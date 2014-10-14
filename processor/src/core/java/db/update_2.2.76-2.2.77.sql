
-- Добавление таблицы соответсвия оборудования школам
create table cf_org_accessories (
  IdOfSourceOrg          BIGINT            NOT NULL,
  IdOfTargetOrg          BIGINT            NOT NULL,
  ACCESSORY_TYPE         INTEGER           NOT NULL,
  ACCESSORY_NUMBER       INTEGER           NOT NULL,
  CONSTRAINT cf_org_accessories_pk PRIMARY KEY (IdOfSourceOrg, IdOfTargetOrg, ACCESSORY_TYPE, ACCESSORY_NUMBER)
);


-- Изменение наименования супер-категорий для правил в соответствии с "Приложение 6. Справка о расходе средств бюджета города Москвы на питание обучающихся"
update cf_discountrules set subcategory='Обучающиеся из соц. незащищ. семей 1-4 кл. (завтрак+обед)' where subcategory='Соц./незащищ. 1-4 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Обучающиеся из соц. незащищ. семей 5-9 кл. (завтрак+обед)' where subcategory='Соц./незащищ. 5-9 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Обучающиеся из соц. незащищ. семей 10-11 кл. (завтрак+обед)' where subcategory='Соц./незащищ. 10-11 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Обучающиеся из многодетных семей 5-9 кл. (завтрак+обед)' where subcategory='Многодетные 5-9 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Обучающиеся из многодетных семей 10-11 кл. (завтрак+обед)' where subcategory='Многодетные 10-11 кл.(завтрак+обед)';
update cf_discountrules set subcategory='Обучающиеся 1-4 кл. (завтрак)' where subcategory='Начальная школа 1-4 кл (завтрак)';