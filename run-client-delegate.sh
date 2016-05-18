#!/bin/sh

echo "Starting the client..."

java -Djava.security.auth.login.config=krb-thrift-login.conf -Dsun.security.krb5.debug=true -Dlog4j.configuration=log4j.properties -classpath /etc/hadoop/conf:krb-thrift/target/krb-thrift-1.0-SNAPSHOT-jar-with-dependencies.jar joshelser.Client -s radicalbit-master -p hbase -i radicalbit-master -d /
