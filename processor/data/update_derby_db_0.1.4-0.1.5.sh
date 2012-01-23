#!/bin/sh
export CLASSPATH=/home/jbosser/jboss/jboss-5.0.1.GA/server/default/deploy/ecafe-processor.ear/lib/derby.jar:/home/jbosser/jboss/jboss-5.0.1.GA/server/default/deploy/ecafe-processor.ear/lib/derbytools.jar
cd /home/jbosser/processor
/usr/local/jre/bin/java -Xms32M -Xmx32M -Xmn12M -Xss1m -Dij.protocol=jdbc:derby: org.apache.derby.tools.ij < /home/jbosser/updates/update_0.1.4-0.1.5/ecafe_processor_derby_0.1.5.sql
 