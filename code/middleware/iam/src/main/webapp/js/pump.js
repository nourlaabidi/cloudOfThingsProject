// Récupérer l'access token et l'email depuis le localStorage
var accesstoken = localStorage.getItem("accesstoken");
var mail = localStorage.getItem("mail");

// Construire l'en-tête d'autorisation
var Authorizationheader = "Bearer " + accesstoken;
console.log("Access token : ", accesstoken); // Affiche le token dans la console pour déboguer

// Attendre que le DOM soit complètement chargé avant de lier les événements
document.addEventListener('DOMContentLoaded', function () {
    // Ajouter un événement au bouton "pump" pour faire une requête AJAX
    document.getElementById('pump').addEventListener('click', function () {
        // Récupérer la dernière ligne de la table sensor
        $.ajax({
            url: 'http://localhost:8080/iam/sensor/last',  // URL pour récupérer le dernier capteur
            type: 'GET',
            headers: {
                'Accept': 'application/json',  // Indique que la réponse attendue est en JSON
                'Authorization': Authorizationheader,  // Utilise l'access token pour l'autorisation
            },
            success: function(sensor) {
                // Utiliser les données du dernier capteur pour l'envoi à l'API de prédiction
                var data = sensor.temperature + "," + sensor.moisture + "," + sensor.humidity; // Format : "temp,moisture,humidity"

                // Envoi de la requête AJAX à l'API de prédiction
                $.ajax({
                    url: 'http://127.0.0.1:5000/predict',  // URL de ton API de prédiction
                    type: 'POST',  // Méthode POST
                    headers: {
                        'Accept': 'application/json',  // Indique que la réponse attendue est en JSON
                        'Authorization': Authorizationheader,  // Utilise l'access token pour l'autorisation
                        'Content-Type': 'text/plain'  // Format de contenu texte brut
                    },
                    data: data,  // Données envoyées à l'API (température, humidité, etc.)
                    success: function(response) {
                        // Lorsque la requête réussit, traiter la réponse
                        console.log("Réponse du serveur : ", response);

                        // Vérifier la valeur de la prédiction et mettre à jour le statut de la pompe
                        if (response.prediction === 1) {
                            document.getElementById('btn-status').innerText = 'Pump ON';
                            document.getElementById('btn-status').style.color = 'green';  // Vert pour "ON"
                        } else {
                            document.getElementById('btn-status').innerText = 'Pump OFF';
                            document.getElementById('btn-status').style.color = 'red';  // Rouge pour "OFF"
                        }
                    },
                    error: function(xhr, status, error) {
                        // Si la requête échoue, afficher un message d'erreur
                        console.error("Erreur lors de la requête : ", error);
                        document.getElementById('btn-status').innerText = 'Error';
                        document.getElementById('btn-status').style.color = 'red';  // Afficher "Erreur" en rouge
                    }
                });
            },
            error: function(xhr, status, error) {
                // Si la requête échoue pour récupérer le dernier capteur
                console.error("Erreur lors de la récupération du dernier capteur : ", error);
            }
        });
    });
});
