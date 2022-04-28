-- Пакет обновлений v 286

ALTER TABLE cf_preorder_complex
    ADD COLUMN toPay integer;
COMMENT ON COLUMN cf_preorder_complex.topay
  IS 'Отметка "К оплате"';

ALTER TABLE cf_preorder_menudetail
    ADD COLUMN toPay integer,
    ADD COLUMN amountToPay integer;
COMMENT ON COLUMN cf_preorder_menudetail.topay IS 'Отметка "К оплате"';
COMMENT ON COLUMN cf_preorder_menudetail.amounttopay IS 'Количество "К оплате"';

alter table cf_orgs
    add column newСashiermode boolean default false;
COMMENT ON COLUMN cf_orgs.newСashiermode
  IS 'Новый режим выдачи (предзаказ)';

ALTER TABLE cf_users
    ADD COLUMN idOfUserCreate bigint,
    ADD COLUMN idOfUserEdit bigint;

COMMENT ON COLUMN cf_users.idOfUserCreate IS 'Идентификатор создателя пользователя (ссылка на таблицу cf_users)';
COMMENT ON COLUMN cf_users.idOfUserEdit IS 'Идентификатор последнего изменившего пользователя (ссылка на таблицу cf_users)';

ALTER TABLE cf_users
    ADD CONSTRAINT cf_users_idofusercreate_fk
        FOREIGN KEY (idOfUserCreate)
            REFERENCES cf_users (idOfUser) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;

ALTER TABLE cf_users
    ADD CONSTRAINT cf_users_idofuseredit_fk
        FOREIGN KEY (idOfUserEdit)
            REFERENCES cf_users (idOfUser) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;

--! ФИНАЛИЗИРОВАН 27.04.2022, НЕ МЕНЯТЬ