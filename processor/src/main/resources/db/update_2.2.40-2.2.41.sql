--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.41
--! Добавление колонок комплексов с 10 по 49 (фактически, с 11 по 50), необходимо для BenefitsRecalculationService
alter table CF_DiscountRules add column Complex10                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex11                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex12                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex13                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex14                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex15                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex16                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex17                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex18                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex19                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex20                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex21                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex22                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex23                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex24                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex25                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex26                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex27                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex28                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex29                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex30                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex31                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex32                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex33                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex34                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex35                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex36                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex37                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex38                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex39                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex40                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex41                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex42                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex43                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex44                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex45                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex46                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex47                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex48                  INTEGER       NOT NULL DEFAULT 0;
alter table CF_DiscountRules add column Complex49                  INTEGER       NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН (Кадыров, 130526) НЕ МЕНЯТЬ