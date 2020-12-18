robocopy src\core\java\ru\axetta\ecafe\processor\core\persistence src\main\resources\hibernate *.hbm.xml /S /MOV
robocopy src\core\java\db src\main\resources\db * /S /MOV

 mvn install:install-file -Dfile=C:\Git\ProcessingSB\processor\build\lib\cryptopro\JCP.jar -DgroupId=CryptoPro -DartifactId=ru.CryptoPro.JCP -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=C:\Git\ProcessingSB\processor\build\lib\cryptopro\XMLDSigRI.jar -DgroupId=CryptoPro -DartifactId=ru.CryptoPro.JCPxml.dsig -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
 mvn install:install-file -Dfile=C:\Git\ProcessingSB\processor\build\lib\cryptopro\JCPxml.jar -DgroupId=CryptoPro -DartifactId=ru.CryptoPro.JCPxml -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true