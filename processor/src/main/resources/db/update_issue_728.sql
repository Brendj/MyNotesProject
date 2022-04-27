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
