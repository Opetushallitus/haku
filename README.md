#haku

### Getting Started

Make sure you have JDK7 on your JAVA_HOME and Java Cryptography Extensions (JCE) installed.

    cd haku
    mvn clean install
    cd haku-app
    mvn tomcat7:run

    http://localhost:9090/haku-app
    
If the site fails to load due to "Input validation failure", make sure you don't have any unwanted cookies stored for localhost on your browser.    

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
