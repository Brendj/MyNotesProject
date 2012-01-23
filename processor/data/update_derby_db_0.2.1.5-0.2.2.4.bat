@echo off
set CLASSPATH=..\build\lib\derby.jar;..\build\lib\derbytools.jar

java -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij < ..\src\core\database\derby\update_0.2.1.5-0.2.2.4\ecafe_processor_derby_0.2.2.4.sql
