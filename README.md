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

### Running and testing

Haku-app uses Spring profiles for running in different modes. Spring profile is selected using system property `spring.profiles.active`, like this: `-Dspring.profiles.active=devluokka`.

Here's a list of profiles we use:

- `default` is the default profile, that is the profile used in the test and production environments. Uses properties files found in ~/oph-configuration
- `dev` uses local mongo database in port 27017 and mocked external dependencies
- `devluokka` uses local mongo database and real external dependencies.Depends on property files in `~/oph-configuration`. See below for
- `it` starts an embedded Mongo server. This profile is used in the integration tests.

When running with Maven, you can use Maven profiles to select the Spring profile.

#### Running from the IDE

You can start a local Tomcat with `it` (integration test) profile so that it uses an embedded Mongo server
by running the `HakuAppTomcat` class.

You can run individual Selenium / Mocha tests from the IDE too. For instance, you may run `MochaIt` class.

#### Running Tomcat locally using Maven

You can start a local Tomcat with `it` (integration test) profile so that it uses an embedded Mongo server:

     (mvn install -DskipTests && cd haku-app && mvn tomcat7:run -Pit)

Debug local Tomcat

    (mvn install -DskipTests && cd haku-app && mvndebug tomcat7:run -Pit)

To use another profile, just replace `-Pit` with `-Pdev` etc. Or use the `tomcat.sh` script as described below.

#### Running haku-app against integration test environment (luokka)

To run locally against external services in the "luokka" environment, you need configuration files in `~/oph-configuration` and
use Spring profile `devluokka`. See instructions below.

1. Optionally set your local env settings to `~/oph-configuration.local` folder
2. remove `~/oph-configuration` folder
3. Copy luokka settings to `~/oph-configuration.luokka` folder from luokka server
4. Modify the luokka settings to use local koulutusinformaatio and local mongo db

    mongodb.oppija.uri=mongodb://localhost:27107
    koulutusinformaatio.ao.resource.url=https\://${host.haku}/ao
    mongodb.url=mongodb://localhost:27017/${mongo.db.name}

    Note: the koulutusinformaatio base url may need also "https" at start, otherwise ajax ui load will fail on local machine when editing preferences.
    koulutusinformaatio.base.url=https://itest-oppija.oph.ware.fi

    Note2: For quicker local tomcat startup, disable scheduled tasks if you dont need them:
    scheduler.runModelUpgrade=false
    scheduler.modelUpgrade.enableV5=false
    scheduler.runEligibilityCheck=false

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

#### Running with QA settings

Just like for "luokka", you need to copy server configuration files from the QA server into `~/oph-configuration.qa`. Then you can use
the `tomcat.sh` script to run the application on your machine with the same settings as it's run in the QA environment. So now you can

`tomcat.sh -q`

And, of course, also

`tomcat.sh -qd`

to debug it. Just connect your debugger to the local port 8000 and enjoy the ride.

#### Running tests

Project has Selenium and Mocha tests for functional testing.

If you already have a Tomcat running, you can run the Mocha tests in your browser at

    http://localhost:9090/haku-app/test/

Or build, start tomcat and run Mochas from the command line:

    (mvn install -DskipTests && cd haku-app && mvn -Pit -Dit.test=MochaIT verify)

Run all tests (including Mocha and Selenium tests):

    MAVEN_OPTS="-Xmx2048m -XX:PermSize=512M" mvn clean verify -Pit

Run single test class:

    -Dit.test=DropdownSelectDefaultValueIT

Debug integration tests:

    mvn clean verify -Pit -Dmaven.failsafe.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 -Xnoagent -Djava.compiler=NONE"
