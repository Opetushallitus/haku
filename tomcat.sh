#!/bin/bash

set -e
set -o pipefail

CONFIG_BASE=${HOME}/oph-configuration
if [ -L $CONFIG_BASE ];
then
	rm $CONFIG_BASE
fi

ln -s ${CONFIG_BASE}.local ${CONFIG_BASE}

clean=""
build=""
debug=""
luokka=""
while getopts "bcdl" opt; do
	case $opt in
	b)
		build="build"
		;;
	c)
		clean="clean"
		;;
	d)
		debug="debug"
		;;
	l)
		luokka="-Dspring.profiles.active=devluokka -Pdevluokka"
		rm $CONFIG_BASE
		ln -s ${CONFIG_BASE}.luokka ${CONFIG_BASE}
		;;
	esac
done

if [ "x$build" == "xbuild" ];
then
	pushd hakemus-api
	mvn $clean install -DskipTests=true
	popd
	pushd haku-app
	mvn $clean install -DskipTests=true
	popd
fi

if [ "x$debug" = "xdebug" ];
then
	export MAVEN_OPTS="-XX:MaxPermSize=1024M -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
fi

MAVEN_OPTS="$MAVEN_OPTS -Xmx4096M" \
	JAVA_OPTS="$JAVA_OPTS -Xmx4096M -Dlog4j.configuration=file://haku-app/src/main/resources/log4j.properties -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:-UseParallelGC" \
	mvn tomcat7:run ${luokka} --projects haku-app -o -DskipTests=true -Dlog4j.debug

