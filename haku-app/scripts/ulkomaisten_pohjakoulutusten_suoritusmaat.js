var ulkomainenYo = function() {
    var dropdownKey = "answers.koulutustausta.pohjakoulutus_yo_ulkomainen_maa";
    var otherKey = "answers.koulutustausta.pohjakoulutus_yo_ulkomainen_maa_muu";
    var q = {};
    var u = {};
    q[dropdownKey] = "XXX";
    q[otherKey] = {$exists: false};
    u[otherKey] = "Tuntematon";
    db.application.update(q, {$set: u}, {multi: true});
};

var ulkomainenKK = function() {
    for (var i = 0; i < 7; i++) {
        var postfix = i === 0 ? "" : i;
        var dropdownKey = "answers.koulutustausta.pohjakoulutus_kk_ulk_maa" + postfix;
        var otherKey = "answers.koulutustausta.pohjakoulutus_kk_ulk_maa_muu" + postfix;
        var q = {};
        var u = {};
        q[dropdownKey] = "XXX";
        q[otherKey] = {$exists: false};
        u[otherKey] = "Tuntematon";
        db.application.update(q, {$set: u}, {multi: true});
    }
};

var ulkomainen = function() {
    for (var i = 0; i < 7; i++) {
        var postfix = i === 0 ? "" : i;
        var oldTextKey = "answers.koulutustausta.pohjakoulutus_ulk_suoritusmaa" + postfix;
        var otherKey = "answers.koulutustausta.pohjakoulutus_ulk_suoritusmaa_muu" + postfix;
        var q = {};
        var u = {};
        q[oldTextKey] = {$exists: true};
        q[otherKey] = {$exists: false};
        u[oldTextKey] = otherKey;
        db.application.update(q, {$rename: u}, {multi: true});
        q = {};
        u = {};
        q[oldTextKey] = {$exists: false};
        q[otherKey] = {$exists: true};
        u[oldTextKey] = "XXX";
        db.application.update(q, {$set: u}, {multi: true});
    }
};
