
CREATE TABLE CF_Account_Transfers (
  IdOfAccountTransfer BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClientBenefactor bigint NOT NULL,
  IdOfClientBeneficiary bigint NOT NULL,
  Reason VARCHAR(256),
  CreatedBy bigint NOT NULL,
  IdOfTransactionOnBenefactor bigint NOT NULL,
  IdOfTransactionOnBeneficiary bigint NOT NULL,
  TransferSum bigint NOT NULL,
  CONSTRAINT cf_account_transfer_pk PRIMARY KEY (IdOfAccountTransfer),
  CONSTRAINT cf_account_transfer_c_bctr_fk FOREIGN KEY (IdOfClientBenefactor) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_transfer_c_bcry_fk FOREIGN KEY (IdOfClientBeneficiary) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_transfer_t_bctr_fk FOREIGN KEY (IdOfTransactionOnBenefactor) REFERENCES cf_transactions (IdOfTransaction),
  CONSTRAINT cf_account_transfer_t_bcry_fk FOREIGN KEY (IdOfTransactionOnBeneficiary) REFERENCES cf_transactions (IdOfTransaction)
);

--! ФИНАЛИЗИРОВАН (Кадыров, 120831) НЕ МЕНЯТЬ