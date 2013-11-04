#haku

### Getting Started

    cd haku
    mvn clean install
    cd haku-app
    mvn tomcat7:run

    http://localhost:8080/haku-app

#### Integration tests

Run all integration tests
    mvn clean package verify -Pintegration-test

Debug integration tests
    -Dmaven.failsafe.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"

Run single test class
    -Dit.test=DropdownSelectDefaultValueIT

More information about failsave maven plugin
    http://maven.apache.org/surefire/maven-failsafe-plugin/

### Documentation
