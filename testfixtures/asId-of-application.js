var processJson = require("./process-stdin");

processJson(function(application) {
  return application["applicationSystemId"];
})
