const express = require('express');
var cors = require('cors');
var mongoose = require('mongoose');
var Thermometer = require('./routes/thermometer');

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
var temperatureSchema = mongoose.Schema({
	date_insert: String,
	temperature: Number,
	humidity: Number,
	light: Number,
});
var TempModel = mongoose.model("temperature", temperatureSchema);

app.get('/', function (req, res) {
	res.send('Hello World!')
});

app.use('/thermometer', Thermometer);
app.set('tempModel', TempModel);

app.post('/thermometer', function(req, res) {
	console.log(req.query);
});


app.listen(3000, function () {
	console.log('Example app listening on port 3000!')
});