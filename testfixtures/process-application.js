var processJson = require("./process-stdin");

if (process.argv.length != 3) {
  console.error("ERROR: Person oid not specified");
  process.exit(1);
}

function changePersonOid(obj) {
  obj.personOid = process.argv[2];
}

function removeEncryptedHetu(obj) {
  delete obj.answers.henkilotiedot.Henkilotunnus
  delete obj.answers.henkilotiedot.Henkilotunnus_digest
}

processJson(function(obj) {
  changePersonOid(obj);
  removeEncryptedHetu(obj);
  return obj;
});