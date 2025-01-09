from flask import Flask, request, jsonify
from joblib import load
import pandas as pd
import numpy as np
import os
from flask_cors import CORS

# Charger le modèle et le scaler
MODEL_DIR = "model/"
model_file = 'Xgboost_model.joblib'
scaler_file = 'scaler.joblib'

model_path = os.path.join(MODEL_DIR, model_file)
scaler_path = os.path.join(MODEL_DIR, scaler_file)

model = load(model_path)
scaler = load(scaler_path)

# Définir les méthodes
def preprocess_input(input_data):
    try:
        # Découpe la chaîne en liste
        my_list = input_data.split(',')
        # Convertit les valeurs en float
        input_data = [float(value) for value in my_list]
        # Crée un DataFrame sans noms de colonnes
        df = pd.DataFrame([input_data])
        # Applique le scaler
        input_data_scaled = scaler.transform(df)
        return input_data_scaled
    except Exception as e:
        raise ValueError(f"Erreur dans le prétraitement des données : {e}")

def make_prediction(input_data_str):
    try:
        print(f"Received input data: {input_data_str}")
        # Prétraiter les données d'entrée
        X = preprocess_input(input_data_str)
        print(f"Preprocessed input data: {X}")
        # Faire des prédictions
        model_predictions = model.predict(X)
        pump_status = int(model_predictions[0])  # Conversion explicite
        return pump_status
    except Exception as e:
        print(f"Error: {e}")
        raise ValueError(f"Erreur dans la prédiction : {e}")

# Créer une application Flask
app = Flask(__name__)
CORS(app) 

@app.route("/predict", methods=["POST"])
def predict_endpoint():
    try:
        # Récupérer les données envoyées via POST
        data = request.data.decode("utf-8")
        if not data:
            return jsonify({"error": "Invalid input, data is required"}), 400

        # Faire une prédiction
        result = make_prediction(data)

        # Retourner le résultat
        return jsonify({"prediction": result})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
