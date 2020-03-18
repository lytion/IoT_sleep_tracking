var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	var alarmModel = req.app.get('alarmModel');
	alarmModel.findOne().sort({date_insert: -1}).exec(function(err, post) {
		res.send(post);
	});
});

router.post('/', function(req, res){
	var alarmModel = req.app.get('alarmModel');
	console.log(req.body);
	var alarmInstance = new alarmModel({
		date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
		alarm_date: req.body['alarm_date']
	});
	alarmInstance.save(function (err) {
		if (err) console.log(err);
		else console.log('alarm save');
	});
	res.send('Alarm post success');
});

//export this router to use in our index.js
module.exports = router;