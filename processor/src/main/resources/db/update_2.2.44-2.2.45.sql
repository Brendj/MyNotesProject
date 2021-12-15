-- Пакет обновлений 2.2.45

-- Добавлена таблица "Правила распределения клиентов".
CREATE TABLE CF_ClientAllocationRule (
  IdOfClientAllocationRule bigserial,
  IdOfSourceOrg bigint not null,
  IdOfDestinationOrg bigint not null,
  GroupFilter varchar(255) not null,
  IsTempClient boolean not null default false,
  CONSTRAINT CF_ClientAllocationRule_PK PRIMARY KEY (IdOfClientAllocationRule),
  CONSTRAINT CF_ClientAllocationRule_Unique UNIQUE (IdOfSourceOrg, IdOfDestinationOrg, GroupFilter),
  CONSTRAINT CF_ClientAllocationRule_SOrg_FK FOREIGN KEY (IdOfSourceOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT CF_ClientAllocationRule_DOrg_FK FOREIGN KEY (IdOfDestinationOrg) REFERENCES cf_orgs (IdOfOrg)
);

--! ФИНАЛИЗИРОВАН (Калимуллин, 130917) НЕ МЕНЯТЬ