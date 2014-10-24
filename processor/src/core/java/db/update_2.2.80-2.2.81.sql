
-- Добавление инструмента для логгирования статистических показателей по работе с внешними системами (на 24.10.14 используется только ЕМП)
create table cf_external_system_stats (
  SystemName character varying(20),
  Instance character varying(20),
  StatisticId INTEGER not null,
  StatisticValue DECIMAL(7, 3),
  CreateDate bigint NOT NULL,

  CONSTRAINT cf_external_system_stats_pk PRIMARY KEY (Instance, SystemName, StatisticId, CreateDate)
);