import paho.mqtt.client as mqtt
import random
import time
import json  # Importation de la bibliothèque JSON

broker = "127.0.0.1"
port = 1883
topic = "garden/sensor/data"

def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))

def simulate_data(temp, soil_moisture, soil_humidity):
    temp += random.uniform(-0.5, 0.5)
    temp = max(0, min(45, temp))  
    
    soil_moisture += random.uniform(-3, 3)
    soil_moisture = max(0, min(90, soil_moisture)) 
    
    soil_humidity += random.uniform(-2, 2)
    soil_humidity = max(20, min(80, soil_humidity)) 
    
    return temp, soil_moisture, soil_humidity

client = mqtt.Client()
client.on_connect = on_connect
client.connect(broker, port, 60)

client.loop_start()  

temp = random.uniform(0, 45)  
soil_moisture = random.uniform(0, 90)  
soil_humidity = random.uniform(20, 80)  

while True:
    temp, soil_moisture, soil_humidity = simulate_data(temp, soil_moisture, soil_humidity)
    
    # Créer un dictionnaire de données à envoyer
    payload = {
        "id": "sensor1",  # Vous pouvez ajouter un identifiant unique pour chaque capteur
        "temperature": temp,
        "moisture": soil_moisture,
        "humidity": soil_humidity
    }
    
    # Convertir en JSON et publier
    payload_json = json.dumps(payload)
    print(f"Sending data: {payload_json}")
    client.publish(topic, payload_json)
    
    time.sleep(10)
