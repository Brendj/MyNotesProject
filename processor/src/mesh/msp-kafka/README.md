# Сервис отправки данных о факте назначения и предаставления МСП

## Основные параметры
* Указание параметров для подключения к БД:
```shell script
$ java -jar msp-kafka.jar --DB_TYPE=postgres-prod
```

* Размер выборки при обработки факта назначения МСП:
```shell script
$ java -jar msp-kafka.jar --SAMPLE_SIZE_ASSIGN=500
```

* Размер выборки при обработки факта предаставления МСП:
```shell script
$ java -jar msp-kafka.jar --SAMPLE_SIZE_SUPPLY=500
```

* Указание расписания при обработки факта назначения МСП:
```shell script
$ java -jar msp-kafka.jar --ASSIGN_CRON="0 */10 * ? * *"
```

* Указание расписания при обработки факта предаставления МСП:
```shell script
$ java -jar msp-kafka.jar --SUPPLY_CRON="0 0 12 6 * ?"
```

Доступны 3 конфигурации:
* postgres-dev - Для подключения к Казанской тестовой среде (включается по умолчанию);
* postgres-devmsk - Для тестирование на тестовом Московском контуре;
* postgres-prod - Для подключения к "боевому" контуру.
  
## Конфигурация проекта
Файлы конфигурации проекта расположены в src\main\resources.  
* Основные конфигурации проекта распаложены в application.properties.
* Конфигурации для подключения к dev-контуру распаложены application-postgres-dev.properties.  
* Конфигурации для подключения к prod-контору распаложены application-postgres-prod.properties.  
