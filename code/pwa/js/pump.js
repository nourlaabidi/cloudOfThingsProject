document.addEventListener('DOMContentLoaded', function () {

    document.getElementById('pump').addEventListener('click', function () {

        const socket = new WebSocket("ws://localhost:8765");

        // Create a WebSocket connection
        const storedSensorData = JSON.parse(localStorage.getItem('sensorData'));
        const btn_status = document.getElementById("btn-status");

        lastSensor = storedSensorData[storedSensorData.length - 1]
        console.log('last',lastSensor)

        let temp = (lastSensor.temperature).toString()
        let hum =  (lastSensor.humidity).toString()
        let mois = (lastSensor.moisture).toString()

        const input = mois + ',' + temp + ',' + hum
        console.log('input',input)

        // Listen for the connection open event
        socket.addEventListener('open', (event) => {
            console.log('WebSocket connection opened:', event);
            // Send a string to the WebSocket server
            const messageToSend = input;
            socket.send(messageToSend);
        });


        // Listen for messages from the WebSocket server
        socket.addEventListener('message', (event) => {
            // Handle the response string from the server
            const status = event.data;
            console.log('Received response from server:', status);
            // Store the response string (you can save it to localStorage or another storage mechanism)
            localStorage.setItem('PumpStatus', status);

            updateUI(status);
        });

        function updateUI(PumpStatus) {
            if (PumpStatus === '1') {
                btn_status.innerHTML = `<span class="status completed">ON</span>`;
            } else if (PumpStatus === '0') {
                btn_status.innerHTML = `<span class="status off">OFF</span>`;
            }
        }


        // Listen for errors
        socket.addEventListener('error', (event) => {
            console.error('WebSocket error:', event);
        });

        // Listen for the connection close event
         socket.addEventListener('close', (event) => {
           console.log('WebSocket connection closed:', event);
         });




    });
});
