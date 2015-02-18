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

#### Running and testing

Project has Selenium and Mocha tests for functional testing.

You can start a local Tomcat with `it` (integration test) profile so that it uses an embedded Mongo server:

     mvn clean package tomcat7:run -Pit -DskipTests

Then you can run the Mocha tests in your browser at

    http://localhost:9090/haku-app/resources/demo/testrunner.html

Or run the whole this from command line with

    (mvn install -DskipTests && cd haku-app && mvn -Pit -Dit.test=MochaIT verify)

Run all integration tests (including Mocha and Selenium tests):

    mvn clean verify -Pit

Run single test class:

    -Dit.test=DropdownSelectDefaultValueIT

Debug integration tests:

    mvn clean verify -Pit -Dmaven.failsafe.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"

Debug local Tomcat

    mvndebug tomcat7:run -Pit

### Running haku-app against integration test environment (luokka)

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