@echo off
set CLASSPATH=..\build\lib\derby.jar;..\build\lib\derbytools.jar
java -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij <..\src\core\database\derby\update_0.1.1-0.1.2\ecafe_processor_derby_gymn19_0.1.2.sql
