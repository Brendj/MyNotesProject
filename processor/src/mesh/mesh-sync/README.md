# Сервис синхранизации даных с МЭШ
Для работы требуется Java 8. В случае отсутствия в системе Maven, в репозитории есть Maven Wrapper.

## Основные команды
* Сборка/пересборка проекта:
```shell script
$ ./mvnw clean package
```
  
* Запуск приложения в dev-контуре:
```shell script
$ ./mvnw spring-boot:run
```
ИЛИ
```shell script
$ java -jar traget/mesh-sync.jar
```
  
* Запуск приложения в prod-контуре:
```shell script
$ ./mvnw -Dspring-boot.run.arguments=--DB_TYPE=postgres-prod spring-boot:run 
```
ИЛИ
```shell script
$ java -jar target/mesh-sync.jar --DB_TYPE=postgres-prod
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
    
Если есть необходимость явно указать с какого раздела (partition) и с какого указателя (offset) 
необходимо начать чтение данных, то необходимо "раскомментить" аннотацию @KafkaListener в классе 
KafkaService и указать в partition/initialOffset нужные значения.