import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.preprocessing import LabelBinarizer
from joblib import dump

# Path to the raw dataset
raw_data_path = r"../data/raw/raw_dataset.csv"

# Read the dataset from the CSV file
df = pd.read_csv(raw_data_path, sep=",")

# Drop irrelevant features from the dataset
irrelevant_features = ['Time', 'Wind gust (Km/h)', 'Wind speed (Km/h)', 'Pressure (KPa)', 'ph', 'rainfall', 'N', 'P', 'K', 'Air temperature (C)', 'Air humidity (%)']
df.drop(columns=irrelevant_features, axis=1, inplace=True)

# Encode the 'Status' variable to binary
encoder = LabelBinarizer()
df['Status'] = encoder.fit_transform(df['Status'])

# Split the dataset into features (X) and target variable (y)
X = df.drop(columns='Status', axis=1)
y = df['Status'].to_frame()

# Scale the feature data
scaler = StandardScaler()
X = scaler.fit_transform(X)
X = pd.DataFrame(X)

# Combine the scaled features and target variable into one dataframe
data = pd.concat([X, y], axis=1)

# Split the data into training and testing sets
train, test = train_test_split(data, test_size=0.3, stratify=data['Status'])

# Define paths to save the processed data and model
MODEL_DIR = "../model/"
PROCESSED_DATA_DIR = "../data/processed/"
train_path = PROCESSED_DATA_DIR + 'train.csv'
test_path = PROCESSED_DATA_DIR + 'test.csv'
scaler_path = MODEL_DIR + 'scaler.joblib'

# Save the scaler model to a file
dump(scaler, scaler_path)

# Save the training and testing data to CSV files
train.to_csv(train_path, index=False)
test.to_csv(test_path, index=False)