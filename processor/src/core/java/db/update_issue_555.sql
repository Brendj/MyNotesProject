ALTER TABLE cf_users
  ADD COLUMN idoforg bigint;
ALTER TABLE cf_users
  ADD COLUMN idofclient bigint;

COMMENT ON COLUMN cf_users.idoforg IS 'Идентификатор организации';
COMMENT ON COLUMN cf_users.idofclient IS 'Идентификатор клиента';
 
ALTER TABLE cf_users
  ADD CONSTRAINT cf_users_idofclient_fk 
  FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE cf_users
  ADD CONSTRAINT cf_users_idoforg_fk 
  FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;