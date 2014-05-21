print("Running user data migration\n");
var apps = db.application.find({'oid' : {$exists : 1}})

while (apps.hasNext()) {

	var app = apps.next();
	var oid = app.oid;

	print("Migrating: "+oid);
	var overriddenAnswers = app.overriddenAnswers;
	if (!overriddenAnswers) {
		overriddenAnswers = {};
	}
	for (var p in app.answers) {
		var phase = app.answers[p];
		for (var key in phase) {
			var index = key.length - 5;
			if (key.indexOf('_user', index) !== -1) {
				var shortKey = key.substring(0, index);
				print(key +' '+shortKey+' -> '+phase[key]);
				overriddenAnswers[shortKey] = phase[key];	
			}
		}
	}
	app.overriddenAnswers = overriddenAnswers;

	db.application.update({'oid' : oid}, {$set : {'overriddenAnswers' : overriddenAnswers}});
}
