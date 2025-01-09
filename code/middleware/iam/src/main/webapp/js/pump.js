const jumbotron = document.querySelector(".jumbotron");


var accesstoken = localStorage.getItem("accesstoken")
var mail = localStorage.getItem("mail")
var Authorizationheader = "Bearer " + accesstoken
console.log(accesstoken)
document.addEventListener('DOMContentLoaded', function () {

    document.getElementById('pump').addEventListener('click', function () {

        $.ajax({
            url: 'http://127.0.0.1:5000/predict',  // URL de ton API
            type: 'POST',  // Méthode POST
            headers: {
                'Accept': 'application/json',  // En-tête pour accepter la réponse JSON
                'Authorization': Authorizationheader
            },
            data: "2,3,5",  // Données à envoyer (en texte brut)

            success: function(response) {
                // Ce qui se passe si la requête réussit
                console.log("Réponse du serveur : ", response);

                // Mettre à jour le statut de la pompe
                if(response.prediction === 1) {
                    document.getElementById('btn-status').innerText = 'Pump ON';
                    document.getElementById('btn-status').style.color = 'green';
                } else {
                    document.getElementById('btn-status').innerText = 'Pump OFF';
                    document.getElementById('btn-status').style.color = 'red';
                }
            },
            error: function(xhr, status, error) {
                // Ce qui se passe en cas d'erreur
                console.error("Erreur lors de la requête : ", error);
                document.getElementById('btn-status').innerText = 'Error';
                document.getElementById('btn-status').style.color = 'red';
            }
        });

    });
});
