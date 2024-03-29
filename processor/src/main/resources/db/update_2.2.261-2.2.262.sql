/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений issue 997

alter table cf_specialdates
    add column staffguid character varying(36),
    add column armlastupdate bigint,
    add column createddate bigint,
    add column lastupdate bigint;

comment on column cf_specialdates.staffguid is 'Гуид сотрудника';
comment on column cf_specialdates.armlastupdate is 'Дата-время изменений на АРМе';
comment on column cf_specialdates.createddate is 'Дата создания записи';
comment on column cf_specialdates.lastupdate is 'Дата последнего изменения';

CREATE TABLE cf_specialdates_history
(
    idofspecialdatehistory bigserial NOT NULL,
    idoforg bigint NOT NULL,
    date bigint NOT NULL,
    isweekend integer NOT NULL,
    deleted integer NOT NULL,
    version bigint NOT NULL,
    comment character varying(256) NOT NULL DEFAULT '',
    idoforgowner bigint NOT NULL,
    idofclientgroup bigint,
    staffguid character varying(36),
    armlastupdate bigint,
    createddate bigint,
    CONSTRAINT cf_specialdates_history_pk PRIMARY KEY (idofspecialdatehistory),
    CONSTRAINT cf_specialdates_history_idoforg_fk FOREIGN KEY (idoforg)
        REFERENCES cf_orgs (idoforg) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_specialdates_history_idoforgowner_fk FOREIGN KEY (idoforgowner)
        REFERENCES cf_orgs (idoforg) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

comment on table cf_specialdates_history is 'История изменений календаря дней питания';

-- Пакет обновлений issue 1013
CREATE SEQUENCE CF_SyncHistory_Exceptions_Id_Gen_seq INCREMENT BY 128;
select setval('CF_SyncHistory_Exceptions_Id_Gen_seq', (select coalesce(max(IdOfSyncHistoryException), 0) + 1 from CF_SyncHistory_Exceptions));

CREATE SEQUENCE CF_ClientPayments_Id_Gen_seq INCREMENT BY 32;
select setval('CF_ClientPayments_Id_Gen_seq', (select coalesce(max(IdOfClientPayment), 0) + 1 from CF_ClientPayments));

CREATE SEQUENCE CF_POS_Id_Gen_seq INCREMENT BY 3;
select setval('CF_POS_Id_Gen_seq', (select coalesce(max(IdOfPos), 0) + 1 from CF_POS));

CREATE SEQUENCE CF_Menu_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Menu_Id_Gen_seq', (select coalesce(max(IdOfMenu), 0) + 1 from CF_Menu));

CREATE SEQUENCE CF_CurrentPositions_Id_Gen_seq INCREMENT BY 3;
select setval('CF_CurrentPositions_Id_Gen_seq', (select coalesce(max(IdOfPosition), 0) + 1 from CF_CurrentPositions));

CREATE SEQUENCE CF_Taloon_Approval_Id_Gen_seq INCREMENT BY 64;
select setval('CF_Taloon_Approval_Id_Gen_seq', (select coalesce(max(idOfTaloonApproval), 0) + 1 from CF_Taloon_Approval));

CREATE SEQUENCE CF_Cards_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Cards_Id_Gen_seq', (select coalesce(max(IdOfCard), 0) + 1 from CF_Cards));

CREATE SEQUENCE cf_synchistory_calc2_Id_Gen_seq INCREMENT BY 256;
select setval('cf_synchistory_calc2_Id_Gen_seq', (select coalesce(max(idOfSyncHistoryCalc), 0) + 1 from cf_synchistory_calc2));

CREATE SEQUENCE cf_preorder_complex_Id_Gen_seq INCREMENT BY 8;
select setval('cf_preorder_complex_Id_Gen_seq', (select coalesce(max(IdOfPreorderComplex), 0) + 1 from cf_preorder_complex));

CREATE SEQUENCE cf_account_operations_Id_Gen_seq INCREMENT BY 3;
select setval('cf_account_operations_Id_Gen_seq', (select coalesce(max(idofaccountoperation), 0) + 1 from cf_account_operations));

CREATE SEQUENCE CF_SchedulerJobs_Id_Gen_seq INCREMENT BY 3;
select setval('CF_SchedulerJobs_Id_Gen_seq', (select coalesce(max(IdOfSchedulerJob), 0) + 1 from CF_SchedulerJobs));

CREATE SEQUENCE cf_regular_preorders_Id_Gen_seq INCREMENT BY 32;
select setval('cf_regular_preorders_Id_Gen_seq', (select coalesce(max(IdOfRegularPreorder), 0) + 1 from cf_regular_preorders));

CREATE SEQUENCE CF_Users_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Users_Id_Gen_seq', (select coalesce(max(IdOfUser), 0) + 1 from CF_Users));

CREATE SEQUENCE cf_menudetails_id_gen_seq INCREMENT BY 512;
select setval('cf_menudetails_id_gen_seq', (select coalesce(max(IdOfMenuDetail), 0) + 1 from cf_menudetails));

CREATE SEQUENCE CF_Settlements_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Settlements_Id_Gen_seq', (select coalesce(max(IdOfSettlement), 0) + 1 from CF_Settlements));

CREATE SEQUENCE cf_org_accessories_Id_Gen_seq INCREMENT BY 3;
select setval('cf_org_accessories_Id_Gen_seq', (select coalesce(max(IdOfAccessory), 0) + 1 from cf_org_accessories));

CREATE SEQUENCE CF_RuleConditions_Id_Gen_seq INCREMENT BY 3;
select setval('CF_RuleConditions_Id_Gen_seq', (select coalesce(max(IdOfRuleCondition), 0) + 1 from CF_RuleConditions));

CREATE SEQUENCE cf_client_guardian_Id_Gen_seq INCREMENT BY 3;
select setval('cf_client_guardian_Id_Gen_seq', (select coalesce(max(IdOfClientGuardian), 0) + 1 from cf_client_guardian));

CREATE SEQUENCE CF_AddPayments_Id_Gen_seq INCREMENT BY 3;
select setval('CF_AddPayments_Id_Gen_seq', (select coalesce(max(IdOfAddPayment), 0) + 1 from CF_AddPayments));

CREATE SEQUENCE CF_ReportHandleRules_Id_Gen_seq INCREMENT BY 3;
select setval('CF_ReportHandleRules_Id_Gen_seq', (select coalesce(max(IdOfReportHandleRule), 0) + 1 from CF_ReportHandleRules));

CREATE SEQUENCE cf_info_messages_Id_Gen_seq INCREMENT BY 3;
select setval('cf_info_messages_Id_Gen_seq', (select coalesce(max(IdOfInfoMessage), 0) + 1 from cf_info_messages));

CREATE SEQUENCE CF_Notifications_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Notifications_Id_Gen_seq', (select coalesce(max(IdOfNotification), 0) + 1 from CF_Notifications));

CREATE SEQUENCE CF_Orgs_Id_Gen_seq;
select setval('CF_Orgs_Id_Gen_seq', (select coalesce(max(IdOfOrg), 0) + 1 from CF_Orgs));

CREATE SEQUENCE CF_Clients_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Clients_Id_Gen_seq', (select coalesce(max(IdOfClient), 0) + 1 from CF_Clients));

CREATE SEQUENCE CF_Calls_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Calls_Id_Gen_seq', (select coalesce(max(IdOfCall), 0) + 1 from CF_Calls));

CREATE SEQUENCE CF_ClientPaymentOrders_Id_Gen_seq INCREMENT BY 32;
select setval('CF_ClientPaymentOrders_Id_Gen_seq', (select coalesce(max(IdOfClientPaymentOrder), 0) + 1 from CF_ClientPaymentOrders));

CREATE SEQUENCE CF_Taloon_Preorder_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Taloon_Preorder_Id_Gen_seq', (select coalesce(max(idOfTaloonPreorder), 0) + 1 from CF_Taloon_Preorder));

CREATE SEQUENCE CF_Persons_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Persons_Id_Gen_seq', (select coalesce(max(IdOfPerson), 0) + 1 from CF_Persons));

CREATE SEQUENCE CF_SyncHistory_Id_Gen_seq INCREMENT BY 512;
select setval('CF_SyncHistory_Id_Gen_seq', (select coalesce(max(IdOfSync), 0) + 1 from CF_SyncHistory));

CREATE SEQUENCE CF_ContragentPayments_Id_Gen_seq INCREMENT BY 3;
select setval('CF_ContragentPayments_Id_Gen_seq', (select coalesce(max(IdOfContragentPayment), 0) + 1 from CF_ContragentPayments));

CREATE SEQUENCE CF_DiscountRules_Id_Gen_seq INCREMENT BY 3;
select setval('CF_DiscountRules_Id_Gen_seq', (select coalesce(max(IdOfRule), 0) + 1 from CF_DiscountRules));

CREATE SEQUENCE CF_Transactions_Id_Gen_seq INCREMENT BY 32;
select setval('CF_Transactions_Id_Gen_seq', (select coalesce(max(IdOfTransaction), 0) + 1 from CF_Transactions));

CREATE SEQUENCE CF_Assortment_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Assortment_Id_Gen_seq', (select coalesce(max(IdOfAst), 0) + 1 from CF_Assortment));

CREATE SEQUENCE CF_Contragents_Id_Gen_seq INCREMENT BY 3;
select setval('CF_Contragents_Id_Gen_seq', (select coalesce(max(IdOfContragent), 0) + 1 from CF_Contragents));

CREATE SEQUENCE cf_wt_discountrules_Id_Gen_seq INCREMENT BY 3;
select setval('cf_wt_discountrules_Id_Gen_seq', (select coalesce(max(IdOfRule), 0) + 1 from cf_wt_discountrules));

--! ФИНАЛИЗИРОВАН 06.05.2021, НЕ МЕНЯТЬ