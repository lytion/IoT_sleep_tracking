var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	var TempModel = req.app.get('tempModel');
	var temperature = 0;
	TempModel.findOne({}, {}, {sort: {'date_insert': -1}}, function (err, post) {
		temperature = post.temperature;
		res.render('thermometer.ejs', {data: temperature});
	});
});

router.post('/', function(req, res){
	var TempModel = req.app.get('tempModel');
	console.log(req.body);
	var TempInstance = new TempModel({
		date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
		temperature: req.body['temperature']
		});
	TempInstance.save(function (err) {
		if (err) console.log(err);
		else console.log('temp save');
	});
	res.send('Temperature post');
});


//export this router to use in our index.js
module.exports = router;