#!/bin/bash

fixturesroot="../hakemus-api/src/main/resources/mongofixtures"
mongoversion=`mongoexport --version`

if [[ $mongoversion =~ 2\.4 ]]; then
  echo Updating fixtures...
else
  echo Mongoexport 2.4 series required
  exit 1
fi

mkdir -p application||0
mkdir -p applicationSystem||0
mkdir -p themequestion||0

if [[ $@ =~ luokka ]]; then
  echo Importing from luokka

  mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.5.2014022711042555034240"}'| node process-appsystem.js  | sed 's/\(.\){"_id"/\1\
{"_id"/g' | sed 's/:\[{/:\[\
{/g' | sed 's/}],/}\
],/g' > $fixturesroot/applicationSystem/2014022711042555034240.json
  mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.29.173465377510"}'| node process-appsystem.js  | sed 's/\(.\){"_id"/\1\
{"_id"/g' | sed 's/:\[{/:\[\
{/g' | sed 's/}],/}\
],/g' > $fixturesroot/applicationSystem/173465377510.json
  mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.5.2013112910452702965370"}'| node process-appsystem.js  | sed 's/\(.\){"_id"/\1\
{"_id"/g' | sed 's/:\[{/:\[\
{/g' | sed 's/}],/}\
],/g' > $fixturesroot/applicationSystem/2013112910452702965370.json
  mongoexport "$@" -d hakulomake -c themequestion --query '{applicationSystemId: "1.2.246.562.5.2014022711042555034240"}' --out $fixturesroot/themequestion/2014022711042555034240.json
  mongoexport "$@" -d hakulomake -c themequestion --query '{applicationSystemId: "1.2.246.562.29.173465377510"}' --out $fixturesroot/themequestion/173465377510.json
  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000877107"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000877107.json
  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000855417"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000855417.json
  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000877699"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000877699.json
  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000877686"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000877686.json

elif [[ $@ =~ reppu ]]; then
  echo Importing from reppu

  mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.5.2013080813081926341927"}'| node process-appsystem.js  | sed 's/\(.\){"_id"/\1\
{"_id"/g' | sed 's/:\[{/:\[\
{/g' | sed 's/}],/}\
],/g' > $fixturesroot/applicationSystem/2013080813081926341927.json

  mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.5.2014020613412490531399"}'| node process-appsystem.js  | sed 's/\(.\){"_id"/\1\
{"_id"/g' | sed 's/:\[{/:\[\
{/g' | sed 's/}],/}\
],/g' > $fixturesroot/applicationSystem/2014020613412490531399.json

  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000441368"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000441368.json

  mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000441371"}' | node process-application.js 1.2.246.562.24.14229104472 > $fixturesroot/application/00000441371.json

else
  echo Please specify mongo params for either luokka or reppu.
fi



echo done.