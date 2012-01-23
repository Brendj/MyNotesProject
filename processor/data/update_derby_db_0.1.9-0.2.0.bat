@echo off
set CLASSPATH=..\build\lib\derby.jar;..\build\lib\derbytools.jar
java -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij <..\src\core\database\derby\update_0.1.9-0.2.0\ecafe_processor_derby_0.2.0.sql
