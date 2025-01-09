  // Attendre que le DOM soit chargé avant d'ajouter un gestionnaire d'événements
  document.addEventListener("DOMContentLoaded", function () {
    const signInButton = document.getElementById("signInId");

    if (signInButton) {
      signInButton.addEventListener("click", function (event) {
        event.preventDefault(); // Empêche le comportement par défaut du lien
        window.location.href = "http://localhost:8080"; // Redirige vers l'URL
      });
    }
  });