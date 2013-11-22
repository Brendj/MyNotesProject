@echo off
set CLASSPATH=..\build\lib\derby.jar;..\build\lib\derbytools.jar
java -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij <..\src\core\database\derby\update_0.0.5-0.1.0\ecafe_processor_derby_0.1.0.sql
