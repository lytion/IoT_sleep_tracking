var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	var roomEnvironmentModel = req.app.get('roomEnvironmentModel');
	roomEnvironmentModel.find({}, {}, {}, function (err, post) {
		// res.render('thermometer.ejs', {data: post});
		res.send(post);
	});
});

router.post('/', function(req, res){
	var roomEnvironmentModel = req.app.get('roomEnvironmentModel');
	console.log(req.body);
	var roomEnvironmentInstance = new roomEnvironmentModel({
		date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
		temperature: req.body['temperature'],
		humidity: req.body['humidity'],
		light: req.body['light'],
		acc_x: req.body['gx'],
		acc_y: req.body['gy'],
		acc_z: req.body['gz'],
		});
	roomEnvironmentInstance.save(function (err) {
		if (err) console.log(err);
		else console.log('temp save');
	});
	res.send('Room environment post success');
});


//export this router to use in our index.js
module.exports = router;