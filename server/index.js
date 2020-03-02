const express = require('express');
var cors = require('cors');
var mongoose = require('mongoose');
var RoomEnvironment = require('./routes/roomenvironment');

const app = express();

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());

// database connection
mongoose.connect('mongodb+srv://simon:IoTProject@iotsleeptracking-dwdhr.gcp.mongodb.net/SleepTracking', {useNewUrlParser: true, useUnifiedTopology: true}, () => {
	console.log('connected to mongodb');
});
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error: '));

// temperature schema + model
var roomEnvironmentSchema = mongoose.Schema({
	date_insert: String,
	temperature: Number,
	humidity: Number,
	light: Number,
	acc_x: Number,
	acc_y: Number,
	acc_z: Number,
});
var roomEnvironmentModel = mongoose.model("room_environment", roomEnvironmentSchema);

app.get('/', function (req, res) {
	res.send('Hello World!')
});

app.use('/roomenvironment', RoomEnvironment);
app.set('roomEnvironmentModel', roomEnvironmentModel);

app.post('/roomenvironment', function(req, res) {
	console.log(req.query);
});


app.listen(3000, function () {
	console.log('Example app listening on port 3000!')
});