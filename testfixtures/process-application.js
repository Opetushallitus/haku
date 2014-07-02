var process = require("./process-stdin");

function changePersonOid(obj) {
  obj.personOid = "1.2.246.562.24.14229104472";
}

process(function(obj, stdout) {
  changePersonOid(obj);
  return obj;
});