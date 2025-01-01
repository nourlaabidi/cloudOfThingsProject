const baseURL = window.location.protocol + "//" + window.location.hostname + ":8080/"
const jumbotron = document.querySelector(".jumbotron");


var accesstoken = localStorage.getItem("accesstoken")
var mail = localStorage.getItem("mail")
var Authorizationheader = "Bearer " + accesstoken
console.log(accesstoken)

$.ajax({
    url: 'http://localhost:8080/smartirrigation-1.0-SNAPSHOT/api/sensor', // Assuming this endpoint returns data for all sensors
    type: 'GET',
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': Authorizationheader // Make sure this is defined elsewhere in your code.
    },
    success: function(res) {
        const sensorsData = res; // Assuming the response is an array of sensors

        let sensorInfoHTML = ''; // Initialize an empty string to store the HTML content for all sensors

        sensorsData.forEach(sensor => {
            const temperature = sensor.tempValue;
            const humidity = sensor.humidityValue;
            const moisture = sensor.moistureValue;

            sensorInfoHTML += `
                    <h2>Sensor ${sensor.id}</h2>
                    <ul class="box-info">
                        <li>
                            <i class='bx bxs-thermometer'></i>
                            <span class="text">
                                <h3>${temperature}</h3>
                                <p>°C Temperature</p>
                            </span>
                        </li>
                        <li>
                            <i class='bx bxs-droplet-half'></i>
                            <span class="text">
                                <h3>${moisture}</h3>
                                <p>Moisture</p>
                            </span>
                        </li>
                        <li>
                            <i class='bx bxs-tachometer'></i>
                            <span class="text">
                                <h3>${humidity}</h3>
                                <p>% Humidity</p>
                            </span>
                        </li>
                    </ul>`;
        });

        // Append all sensor info HTML to a container element (e.g., #sensorsContainer)
        $('#sensorsContainer').html(sensorInfoHTML);
    },
    error: function(err) {
        // Handle error
        $('#sensorsContainer').html("<h3>Error getting sensors information</h3>");
    }
});



