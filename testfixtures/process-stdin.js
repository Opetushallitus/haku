module.exports = function(callback) {
  var fs = require("fs"),
    stdin = process.stdin,
    stdout = process.stdout;

  inputChunks = [];

  stdin.resume();
  stdin.setEncoding('utf8');

  stdin.on('data', function (chunk) {
      inputChunks.push(chunk);
  });

  stdin.on('end', function () {
      var result = JSON.stringify(callback(JSON.parse(inputChunks.join("")), stdout));
      stdout.write(result);
  });
}