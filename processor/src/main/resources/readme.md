Файлы keystore и truststore
Файл
server.ssl.key-store=file:/home/cert/novayashkola.keystore берется из конфига jboss:
 certificate-key-file="C:\JBosser\7.1.1\standalone\configuration\novayashkola.keystore"

Файл
server.ssl.trust-store=file:/home/cert/keystore.jks генерируется скриптом:
set CN=axetta_office
keytool -genkeypair -keystore keystore.jks -dname "CN=%CN%" -keypass changeit -storepass changeit
keytool -importcert -alias tomcat -file ispp_axetta_office.cer -keystore keystore.jks -keypass 1 -storepass changeit
pause

где файл ispp_axetta_office.cer - сертификат, сгенерированный через makeca.bat

=================

Переопределение security ограничений
jdk.certpath.disabledAlgorithms=MD2, DSA, RSA keySize < 1024
jdk.tls.disabledAlgorithms=SSLv3, RC4, DH keySize < 1024
задается в отдельном файле, расположение к которому задается в опции запуска
 -Djava.security.properties=config/security.properties