@echo off
set CLASSPATH=..\build\lib\derby.jar;..\build\lib\derbytools.jar
java -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij <..\src\core\database\derby\update_0.1.6-0.1.7\ecafe_processor_derby_0.1.7.sql
