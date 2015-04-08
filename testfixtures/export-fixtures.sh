#!/bin/bash

export_application_system() {
    ID="$1"
    shift
    mongoexport $@ -d hakulomake -c applicationSystem \
                --query "{_id: \"${ID}\"}" \
        | node process-appsystem.js \
        | sed 's/\(.\){"_id"/\1\{"_id"/g' \
        | sed 's/:\[{/:\[\{/g' \
        | sed 's/}],/}\],/g'
}

export_theme_questions() {
    ID="$1"
    shift
    mongoexport $@ -d hakulomake -c themequestion \
                --query "{applicationSystemId: \"${ID}\"}"
}

export_application() {
    ID="$1"
    PERSON_OID="$2"
    shift 2
    mongoexport $@ -d hakulomake -c application \
                --query "{oid: \"${ID}\"}" \
        | node process-application.js ${PERSON_OID}
}

FIXTURES_ROOT="$(dirname "$0")/../hakemus-api/src/main/resources/mongofixtures"
APPLICATION_OID="$1"
PERSON_OID="$2"
shift 2

if [[ $@ =~ luokka ]] || [[ $@ =~ "qa.oph" ]]; then
  echo Importing from luokka

  FILENAME=$(echo ${APPLICATION_OID} | cut -d'.' -f6)
  ASID=$(export_application ${APPLICATION_OID} ${PERSON_OID} "$@" \
                | tee ${FIXTURES_ROOT}/application/${FILENAME}.json \
                | node asId-of-application.js \
                | tr -d '"')

  FILENAME=$(echo ${ASID} | cut -d'.' -f6)
  export_application_system ${ASID} "$@" \
    > ${FIXTURES_ROOT}/applicationSystem/${FILENAME}.json

  export_theme_questions ${ASID} "$@" \
    > ${FIXTURES_ROOT}/themequestion/${FILENAME}.json
else
  echo Please specify mongo params for luokka.
fi
