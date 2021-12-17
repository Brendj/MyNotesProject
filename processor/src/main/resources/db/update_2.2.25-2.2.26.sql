ALTER TABLE cf_staffs ADD COLUMN HashCode integer NOT NULL DEFAULT 0;
ALTER TABLE cf_orgs ADD COLUMN RefectoryType integer NULL;


CREATE TABLE CF_Account_Refund (
  IdOfAccountRefund BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClient bigint NOT NULL,
  Reason VARCHAR(256),
  CreatedBy bigint NOT NULL,
  IdOfTransaction bigint NOT NULL,
  RefundSum bigint NOT NULL,
  CONSTRAINT cf_account_refund_pk PRIMARY KEY (IdOfAccountRefund),
  CONSTRAINT cf_account_refund_clt_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_refund_tr_fk FOREIGN KEY (IdOfTransaction) REFERENCES cf_transactions (IdOfTransaction)
);

CREATE INDEX cf_account_refund_idofcl_idx on CF_Account_Refund(IdOfClient);



-- создать индексы если не созданы
create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_enterevents_idofclient_idx'';
 if v_exists = 0 then
  create index cf_enterevents_idofclient_idx on cf_enterevents(idOfClient);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_enterevents_idevtdt_idx'';
 if v_exists = 0 then
  create index cf_enterevents_idevtdt_idx on cf_enterevents(IdOfClient, EvtDateTime);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_transactions_idofclient_idx'';
 if v_exists = 0 then
  create index cf_transactions_idofclient_idx on cf_transactions(idofclient);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_clientsms_idofclient_idx'';
 if v_exists = 0 then
  create index cf_clientsms_idofclient_idx on cf_clientsms(idofclient);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_clientpayments_idofca_idx'';
 if v_exists = 0 then
  create index cf_clientpayments_idofca_idx on cf_clientpayments(idofcontragent);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();

create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_clientpayments_crdate_idx'';
 if v_exists = 0 then
  create index cf_clientpayments_crdate_idx on cf_clientpayments(createddate);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();




create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_orderdetails_fk_idx'';
 if v_exists = 0 then
  create index cf_orderdetails_fk_idx on cf_orderdetails(idoforg, idoforder);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();



create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_menudetail_idofmenu_idx'';
 if v_exists = 0 then
  create index cf_menudetail_idofmenu_idx on cf_menudetails(idOfMenu);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();



create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_cards_idofclient_idx'';
 if v_exists = 0 then
  create index cf_cards_idofclient_idx on cf_cards(idofclient);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_transactions_trdate_idx'';
 if v_exists = 0 then
  create index cf_transactions_trdate_idx on cf_transactions(transactiondate);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();


create or replace function inline_0() returns integer as '
declare v_exists integer;
begin
 select into v_exists count(*) from pg_class where relname = ''cf_clientpayments_idca_idpay_idx'';
 if v_exists = 0 then
  create index cf_clientpayments_idca_idpay_idx on cf_clientpayments(idofcontragent, idofpayment);
 end if;
return null;
end;' language 'plpgsql';

select inline_0();
drop function inline_0();

--! ФИНАЛИЗИРОВАН (Кадыров, 121014) НЕ МЕНЯТЬ