#!/bin/sh

echo "Starting hdfs test..."

java -Djava.security.auth.login.config=krb-thrift-login.conf -Dsun.security.krb5.debug=true -Dlog4j.configuration=log4j.properties -classpath /etc/hadoop/conf/:/home/alluxio/hadoop/share/hadoop/common/*:/home/alluxio/hadoop/share/hadoop/common/lib/*:/home/alluxio/hadoop/share/hadoop/hdfs/*:/home/alluxio/hadoop/share/hadoop/hdfs/lib/*:krb-thrift/target/krb-thrift-1.0-SNAPSHOT-jar-with-dependencies.jar joshelser.HDFSAccess
