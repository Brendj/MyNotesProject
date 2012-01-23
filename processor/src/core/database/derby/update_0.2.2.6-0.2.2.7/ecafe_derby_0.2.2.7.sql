CONNECT 'jdbc:derby:ecafe_processor_db';

alter table CF_OrderDetails alter column MenuGroup VARCHAR(60)   NOT NULL;
