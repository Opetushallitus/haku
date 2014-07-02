var process = require("./process-stdin");

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
  obj.applicationPeriods[0]["start"]["$date"] = "2014-07-01T08:00:31.839+0300";
  obj.applicationPeriods[0]["end"]["$date"] = "2014-12-01T08:00:31.839+0300";
}

process(function(obj, stdout) {
  stripLists(obj);
  changePeriods(obj);
  return obj;
});