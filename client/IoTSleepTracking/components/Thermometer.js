import React, {Component} from 'react';
import {View, Text} from "react-native";

class Thermometer extends Component {
	constructor(props) {
		super(props);
		this.state = {
			data : {}
		};

	}

	componentDidMount(){
		this.getThermometerData();
		this.timer = setInterval(()=> this.getThermometerData(), 60000);
	}

	async getThermometerData() {
		console.log('passe');
		fetch('http://129.12.128.210:3000/thermometer', {
			method: 'GET'
		})
			.then((response) => response.json())
			.then((responseJson) => {
				// console.log(responseJson);
				this.setState({
					data: responseJson
				})
			})
			.catch((error) => {
				console.error(error);
			});
	};

	render () {
		console.log(this.state.data);
		return (
		<View>
			{this.state.data.length > 0 ?
			<Text>Current temperature: {this.state.data[this.state.data.length-1].temperature} °C</Text> : null}
			{this.state.data.length > 0 ?
			<Text>Current temperature: {this.state.data[this.state.data.length-1].humidity} %</Text> : null}
		</View>
		)
	};
}

export default Thermometer