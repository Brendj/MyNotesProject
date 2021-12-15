ALTER TABLE cf_clients ADD COLUMN ExternalId BIGINT UNIQUE;
ALTER TABLE cf_clients ADD COLUMN ClientGUID VARCHAR(40) UNIQUE;

CREATE index "cf_clients_externalid_idx" ON CF_Clients (ExternalId);
CREATE index "cf_clients_clientguid_idx" ON CF_Clients (ClientGUID);
