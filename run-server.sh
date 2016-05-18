#!/bin/sh

echo "Starting the server..."

nohup java -Djava.security.auth.login.config=krb-thrift-login.conf -Dlog4j.configuration=log4j.properties -Dsun.security.krb5.debug=true -cp /etc/hadoop/conf:/home/alluxio/hadoop/share/hadoop/common/*:/home/alluxio/hadoop/share/hadoop/common/lib/*:/home/alluxio/hadoop/share/hadoop/hdfs/*:/home/alluxio/hadoop/share/hadoop/hdfs/lib/*:krb-thrift/target/krb-thrift-1.0-SNAPSHOT-jar-with-dependencies.jar joshelser.Server -p hbase/radicalbit-master@RADICALBIT.IO -k /home/alluxio/krb-thrift/hbase.service.keytab &
