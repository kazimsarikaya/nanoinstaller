#!/bin/bash

cat > conf/config.properties <<EOF
org.osgi.framework.storage.clean=onFirstInit
felix.auto.deploy.action=install,start
felix.auto.deploy.dir=bundle
felix.log.level=3
org.osgi.framework.startlevel.beginning=4
felix.startlevel.bundle=4
felix.fileinstall.poll=1000
felix.fileinstall.dir=apps
felix.fileinstall.log.level=3
felix.fileinstall.bundles.updateWithListeners=true
org.osgi.service.http.port=8080
obr.repository.url=http://felix.apache.org/obr/releases.xml

EOF

echo "felix.auto.install.1=`find core -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties
echo "felix.auto.install.2=`find nanohttpd-core -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties
echo "felix.auto.install.3=`find nanohttpd-services -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties

echo >> conf/config.properties

echo "felix.auto.start.1=`find core -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties
echo "felix.auto.start.2=`find nanohttpd-core -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties
echo "felix.auto.start.3=`find nanohttpd-services -type f |sed 's/^/file:.\//g' |xargs echo`" >> conf/config.properties

java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 -jar bin/felix-main.jar
