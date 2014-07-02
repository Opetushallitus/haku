#!/bin/bash

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

mongoexport "$@" -d hakulomake -c applicationSystem --query '{_id:"1.2.246.562.5.2014022711042555034240"}' | node process-appsystem.js > applicationSystem/2014022711042555034240.json

mongoexport "$@" -d hakulomake -c themequestion --query '{applicationSystemId: "1.2.246.562.5.2014022711042555034240"}' --out themequestion/2014022711042555034240.json

mongoexport "$@" -d hakulomake -c application --query '{oid: "1.2.246.562.11.00000877107"}' | node process-application.js > application/00000877107.json

echo done.