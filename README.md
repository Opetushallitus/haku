#haku

### Getting Started

Make sure you have JDK7 on your JAVA_HOME and Java Cryptography Extensions (JCE) installed.

    cd haku
    mvn clean install
    cd haku-app
    mvn tomcat7:run

    http://localhost:9090/haku-app

If the site fails to load due to "Input validation failure", make sure you don't have any unwanted cookies stored for localhost on your browser.

Lomakkeet hakijalle: http://localhost:9090/haku-app/lomake/

#### Integration tests

Project has Selenium and Mocha tests for functional testing.

Run Mocha tests from:

    http://localhost:9090/haku-app/resources/demo/testrunner.html

    or from command line with "mvn -Pintegration-test -Dit.test=MochaIT verify"

Run all integration tests:

    mvn clean verify -Pintegration-test

Debug integration tests:

    -Dmaven.failsafe.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"

Run single test class:

    -Dit.test=DropdownSelectDefaultValueIT

More information about failsave maven plugin:

    http://maven.apache.org/surefire/maven-failsafe-plugin/

### Running against integration test environment (luokka)

1. Optionally set your local env settings to `~/oph-configuration.local` folder
2. remove `~/oph-configuration` folder
3. Copy luokka settings to `~/oph-configuration.luokka` folder from luokka server
4. Modify the luokka settings to use local koulutusinformaatio and local mongo db

    mongodb.oppija.uri=mongodb://localhost:27107
    koulutusinformaatio.ao.resource.url=https\://${host.haku}/ao
    mongodb.url=mongodb://localhost:27017/${mongo.db.name}

then you can start the server with local settings by

`tomcat.sh`

and luokka settings by

`tomcat.sh -l`

The script symlinks the `~/oph-configuration` to right configs.

Script takes also these parameters:

    c = clean
    b = build
    d = debug

so you can eg. clean, build & debug with luokka settings by

`tomcat.sh -cbdl`