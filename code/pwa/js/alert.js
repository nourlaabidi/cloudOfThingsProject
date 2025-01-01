const baseURL = "ws://" + window.location.hostname+ ":8080/"
document.addEventListener('DOMContentLoaded', function () {


    // Connect to WebSocket
    const socket = new WebSocket(baseURL+'pushes');
    console.log("websocket", socket);

    // Vérifiez si le tableau existe dans localStorage
    const storedSensorData = [];
    const sensorDataContainer = document.getElementById('sensorsContainer');

    // Listen for WebSocket messages
    socket.addEventListener('message', function (event) {
        try{
        const sensorData = JSON.parse(event.data);
        console.log("sensorData", sensorData);

        // Ajoutez la nouvelle donnée au tableau existant
        storedSensorData.push(sensorData);

        // Mettez à jour le localStorage avec le nouveau tableau
        localStorage.setItem('sensorData', JSON.stringify(storedSensorData));
     } catch (error) {
        console.error('Error parsing JSON:', error);
     }
    });

     // Get the container when needed
    window.onload = async () => {
        try {
        const storedSensorData = JSON.parse(localStorage.getItem('sensorData'));
        displaySensorData(storedSensorData);

        } catch (error) {
        console.error('Error fetching users:', error);
        }

    };

    function displaySensorData(sensorData) {
        console.log("sensordata",sensorData);
        if (!sensorDataContainer) {
            console.error("Container element is null. Aborting displaySensorData.");
            return;
        }  
        // Assurez-vous que sensorData est un tableau avant de l'itérer
        if (Array.isArray(sensorData)) {
            const sensorDataElement = document.createElement('div');
            sensorData.forEach(sensor => {
                const temperature = sensor.temperature;
                const humidity = sensor.humidity;
                const moisture = sensor.moisture;
                sensorDataElement.innerHTML = `
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
            sensorDataContainer.appendChild(sensorDataElement);
            // // Append all sensor info HTML to a container element (e.g., #sensorsContainer)
            // $('sensorsContainer').html(sensorInfoHTML);
        }
        }
        });
