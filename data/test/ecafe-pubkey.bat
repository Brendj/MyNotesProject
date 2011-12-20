@echo off
set LOCAL_JRE=C:\Program Files\Java\jre1.5.0_19
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE%\lib\charsets.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\deploy.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\javaws.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\jce.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\jsse.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\plugin.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\rt.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\ext\dnsns.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\lib\ext\localedata.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\ext\sunjce_provider.jar
set LOCAL_JRE_CLASSPATH=%LOCAL_JRE_CLASSPATH%;%LOCAL_JRE%\ext\sunpkcs11.jar

set ECAFE_PROCESSOR_HOME=D:\work\java\ecafe\processor
set ECAFE_PROCESSOR_LIB=%ECAFE_PROCESSOR_HOME%\build\lib
set JAR_CLASSPATH=%ECAFE_PROCESSOR_LIB%\commons-io-1.4.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-httpclient-3.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-codec-1.3.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-adapters-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-api-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-lang-2.4.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\xmlsec.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\xmldsig.jar

set JAR_FILENAME=%ECAFE_PROCESSOR_HOME%\target\out\ecafe-pubkey.jar

"%LOCAL_JRE%\bin\java.exe" -Dfile.encoding=UTF-8 -classpath "%LOCAL_JRE_CLASSPATH%;%JAR_CLASSPATH%;%JAR_FILENAME%" ru.axetta.ecafe.pubkey.Main %1 %2 %3 %4 %5 %6 %7 %8 %9