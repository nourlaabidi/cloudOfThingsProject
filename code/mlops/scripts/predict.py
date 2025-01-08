import asyncio
import pandas as pd
import numpy as np
from joblib import load
import json
import os
MODEL_DIR = "../model/"
# Set path for the input (model)
model_file = 'Xgboost_model.joblib'
model_path = MODEL_DIR + model_file
Xgboost_model = load(model_path)

scaler_file = 'scaler.joblib'
scaler_path = MODEL_DIR + scaler_file
scaler = load(scaler_path)
def preprocess_input(input_data):
    # Découpe la chaîne en liste
    my_list = input_data.split(',')
    # Convertit les valeurs en float
    input_data = [float(value) for value in my_list]
    # Crée un DataFrame sans noms de colonnes
    df = pd.DataFrame([input_data])
    # Applique le scaler
    input_data_scaled = scaler.transform(df)
    return input_data_scaled
def predict(input_data_str):
    try:
        print(f"Received input data: {input_data_str}")
        # Preprocess the input and make predictions
        X = preprocess_input(input_data_str)
        print(f"preprocessed input data: {X}")
        model_predictions = Xgboost_model.predict(X)
        pump_status = model_predictions[0]
        return pump_status
    except Exception as e:
        print(f"Error: {e}")
        
while True:  # Boucle infinie pour accepter de nouvelles entrées
    #input doit etre "Soil Humidity, Soil Moisture, Temperature"
    input_data_str = input("Enter input data (Soil Humidity, Soil Moisture, Temperature): ")
    if input_data_str.lower() == 'exit':  # Si l'utilisateur entre 'exit', la boucle se termine
        break
    result = predict(input_data_str)
    print(f"Predicted pump status: {result}")
