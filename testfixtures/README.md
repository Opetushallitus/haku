# Scripts for exporting fixture data

`<project-root>/hakemus-api/src/main/resources/mongofixtures` contains fixture
data for the hakulomake mongo DB. This data is used by:
* omatsivut while running tests

`export-fixtures.sh <application OID> <person OID> -u <user> -p <passwd> -h 'hakemusmongodb1.qa.oph.ware.fi'`
exports from QA mongo the given application and the related applicationSystem
and themequestion documents. The person OID of the application is set to the
given OID.
