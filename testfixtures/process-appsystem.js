var processJson = require("./process-stdin");

function stripLists(obj) {
  (function stripListsFrom(children) {
    children.forEach(function(child) {
      if (child.options != null) {
          var finnishValue = child.options.filter(function(item) { return ["FI", "FIN", "00100", "SV", "EN"].indexOf(item.value) >= 0 });
          if (finnishValue.length > 0)
            child.options = finnishValue;
          else
            child.options = child.options.splice(0, 3);
      }

      if (child.i18nText != null) {
        delete child.i18nText.translations.en;
        delete child.i18nText.translations.sv;
      }

      stripListsFrom(child.children);
    });
  })(obj.form.children);
}

function changePeriods(obj) {
  obj.applicationPeriods[0]["start"]["$date"] = new Date("2014-07-01T08:00:31.839+0300").getTime();
  obj.applicationPeriods[0]["end"]["$date"] = new Date("2100-12-01T08:00:31.839+0300").getTime();
}

processJson(function(obj) {
  stripLists(obj);
  changePeriods(obj);
  return obj;
});