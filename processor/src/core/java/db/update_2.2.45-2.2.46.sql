CREATE TABLE cf_temporary_orders (
  IdOfOrg bigint not null,
  IdOfClient bigInt not null,
  IdOfComplex int not null,
  PlanDate bigint not null,
  Action int not null,
  IdOfReplaceClient bigInt,
  CreationDate bigint not null,
  ModificationDate bigint,
  IdOfOrder bigint default null,
  IdOfUser bigint not null,
  CONSTRAINT cf_temporary_orders_pk PRIMARY KEY (IdOfOrg, IdOfClient, IdOfComplex, PlanDate),
  CONSTRAINT cf_temporary_orders_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_temporary_orders_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

CREATE TABLE cf_thin_client_users (
  IdOfClient bigint not null,
  UserName varchar(64) not null,
  Password varchar(128) not null,
  Role int not null default 1,
  CreationDate bigint not null,
  ModificationDate bigint,
  CONSTRAINT cf_thin_client_users_pk PRIMARY KEY (UserName),
  CONSTRAINT cf_thin_client_users_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);
insert into cf_thin_client_users values (1032, 'testerov', 'R2hiZHRuMDA=', 1, 1378976400000, null) values (IdOfOrg, IdOfClient, UserName, Password, Role, CreationDate, ModificationDate);

-- Поправка бага ECAFE-1179
ALTER TABLE cf_reportinfo ALTER COLUMN reportname TYPE character varying(512);