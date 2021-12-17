ALTER TABLE CF_Orgs DROP CONSTRAINT CF_Orgs_ContractId;
CREATE index "cf_menudetail_localid_idx" ON CF_MenuDetails (LocalIdOfMenu);