var process = require("./process-stdin");

function changePersonOid(obj) {
  obj.personOid = "1.2.246.562.24.14229104472";
}

function removeEncryptedHetu(obj) {
  delete obj.answers.henkilotiedot.Henkilotunnus
  delete obj.answers.henkilotiedot.Henkilotunnus_digest
}

process(function(obj, stdout) {
  changePersonOid(obj);
  removeEncryptedHetu(obj);
  return obj;
});