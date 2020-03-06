var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
	var userMovementModel = req.app.get('userMovementModel');
	userMovementModel.find({}, {}, {}, function (err, post) {
		res.send(post);
	});
});

router.post('/', function(req, res){
	var userMovementModel = req.app.get('userMovementModel');
	console.log(req.body);
	var userMovementInstance = new userMovementModel({
		date_insert: new Date().toISOString().replace('T', ' ').substr(0, 19),
		movement: req.body['movement'],
	});
	userMovementInstance.save(function (err) {
		if (err) console.log(err);
		else console.log('user movement saved');
	});
	res.send('User movement post success');
});


//export this router to use in our index.js
module.exports = router;