set LOCAL_JRE=D:\tools\jdk1.6.0_21\
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

set ECAFE_PROCESSOR_LIB=D:\Work.dwc\SVN\ecafe-jb7\build\lib\
set JAR_CLASSPATH=%ECAFE_PROCESSOR_LIB%\commons-io-1.4.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-httpclient-3.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-codec-1.3.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-adapters-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-api-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-logging-1.1.1.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\commons-lang-2.4.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\xmlsec.jar
set JAR_CLASSPATH=%JAR_CLASSPATH%;%ECAFE_PROCESSOR_LIB%\xmldsig.jar

set JAR_FILENAME=ecafe-synctest.jar

set URL=https://localhost:8443/processor/sync
set FILE_IN=sync_in.xml
set PRIV_KEY=privateKey.txt
set PUB_KEY=publicKey.txt
set FILE_OUT=sync_out.xml

"%LOCAL_JRE%\bin\java.exe" -Dfile.encoding=UTF-8 -classpath "%LOCAL_JRE_CLASSPATH%;%JAR_CLASSPATH%;%JAR_FILENAME%" ru.axetta.ecafe.synctest.Main %URL% %FILE_IN% %PRIV_KEY% %FILE_OUT% %PUB_KEY% admin admin -disable_cert_check
