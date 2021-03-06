const express = require('express');
var cors = require('cors');
var mongoose = require('mongoose');
var RoomEnvironment = require('./routes/RoomEnvironment');
var UserMovement = require('./routes/UserMovement');
var Alarm = require('./routes/Alarm');
var SmartAlarm = require('./routes/SmartAlarm');

const app = express();

const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());
const port = process.env.PORT || 5000;

// database connection
mongoose.connect('mongodb+srv://simon:JV7c8@iotsleeptracking-abyfb.gcp.mongodb.net/SleepTracking', {useNewUrlParser: true, useUnifiedTopology: true}, () => {
	console.log('connected to mongodb');
});
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error: '));
/**
 * roomEnvironment schema + model
 */
var roomEnvironmentSchema = mongoose.Schema({
	date_insert: String,
	temperature: Number,
	humidity: Number,
	light: Number,
});
var roomEnvironmentModel = mongoose.model("room_environment", roomEnvironmentSchema);

app.use('/roomenvironment', RoomEnvironment);
app.set('roomEnvironmentModel', roomEnvironmentModel);

app.post('/roomenvironment', function(req, res) {
	console.log(req.query);
});

/**
 * user movement schema + model
 */
var userMovementSchema = mongoose.Schema({
	date_insert: String,
	movement: Number,
});
var userMovementModel = mongoose.model("user_movement", userMovementSchema);

app.use('/usermovement', UserMovement);
app.set('userMovementModel', userMovementModel);

app.post('/usermovement', function(req, res) {
	console.log(req.query);
});

/**
 *  alarm schema + model
 */
var alarmSchema = mongoose.Schema({
	date_insert: String,
	alarm_date: String,
	interval: Number,
});
var alarmModel = mongoose.model("alarm", alarmSchema);

app.use('/alarm', Alarm);
app.set('alarmModel', alarmModel);

app.post('/alarm', function(req, res) {
	console.log(req.query);
});

app.use('/smartalarm', SmartAlarm);

app.get('/', function (req, res) {
	res.send('Hello World!')
});

app.listen(port, () => console.log(`server has started on port ${port}`));