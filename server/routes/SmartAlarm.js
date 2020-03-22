var express = require('express');
var moment = require('moment');
var router = express.Router();

router.get('/', function(req, res){
	var alarmModel = req.app.get('alarmModel');
	alarmModel.findOne().sort({_id: -1}).exec(function(err, post) {
		// res.render('thermometer.ejs', {data: post});
		var date = new Date();
		var alarmTime = moment(post.alarm_date, 'HH:mm');
		var currentTime = moment(date.getHours()+':'+date.getMinutes(), 'HH:mm');
		var diff = alarmTime.diff(currentTime, 'minutes');
		// console.log(post);
		console.log("alarmTIme: "+post.alarm_date);
		console.log("currentTime: "+date.getHours()+':'+date.getMinutes());
		console.log("interval: "+post.interval);
		console.log("diff: "+diff);
		if (diff > 1 && diff < post.interval) {
			var smartAlarm = date.getHours()+':'+ (date.getMinutes()+1);
			var alarmInstance = new alarmModel({
				date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
				alarm_date: smartAlarm,
				interval: 0
			});
			alarmInstance.save(function (err) {
				if (err) console.log(err);
				else console.log('alarm save');
			});
			res.send("SmartAlarm set to: " + smartAlarm);
		}
		else
			res.send("Not authorized to set an alarm on this interval");
	});
});

//export this router to use in our index.js
module.exports = router;