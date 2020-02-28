var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	var TempModel = req.app.get('tempModel');
	TempModel.find({}, {}, {}, function (err, post) {
		// res.render('thermometer.ejs', {data: post});
		res.send(post);
	});
});

router.post('/', function(req, res){
	var TempModel = req.app.get('tempModel');
	console.log(req.body);
	acc = req.body['acceleration'].split(';');
	var TempInstance = new TempModel({
		date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
		temperature: req.body['temperature'],
		humidity: req.body['humidity'],
		light: req.body['light'],
		acc_x: acc[0],
		acc_y: acc[1],
		acc_z: acc[2],
		});
	TempInstance.save(function (err) {
		if (err) console.log(err);
		else console.log('temp save');
	});
	res.send('Temperature post');
});


//export this router to use in our index.js
module.exports = router;