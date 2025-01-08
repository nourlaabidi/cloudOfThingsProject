import pandas as pd
import json
import os
from joblib import dump
from sklearn.model_selection import StratifiedKFold, cross_val_score
from xgboost import XGBClassifier

# Set path to input data directory and file
PROCESSED_DATA_DIR = "../data/processed/"
train_data_file = 'train.csv'
train_data_path = PROCESSED_DATA_DIR + train_data_file

# Read the training data from CSV file
df = pd.read_csv(train_data_path, sep=",")

# Split data into features (X_train) and target variable (y_train)
X_train = df.drop('Status', axis=1)
y_train = df['Status']

# Initialize the XGBoost classifier with specified hyperparameters
Xgboost_model = XGBClassifier(random_state=21, n_estimators=200, max_depth=5, learning_rate=0.2)

# Fit the model on the training data
Xgboost_model = Xgboost_model.fit(X_train, y_train)

# Perform cross-validation to evaluate the model
cv = StratifiedKFold(n_splits=3)
val_model = cross_val_score(Xgboost_model, X_train, y_train, cv=cv).mean()

# Store the validation accuracy in a dictionary
train_metadata = {
    'validation_acc': val_model
}

# Set path to output model directory and file
MODEL_DIR = "../model/"
model_name = 'Xgboost_model.joblib'
model_path = MODEL_DIR + model_name

# Serialize and save the trained model to disk
dump(Xgboost_model, model_path)

# Set path to output results directory and file
RESULTS_DIR = "../results/"
train_results_file = 'train_metadata.json'
results_path = os.path.join(RESULTS_DIR, train_results_file)

# Serialize and save the training metadata (validation accuracy) to disk
with open(results_path, 'w') as outfile:
    json.dump(train_metadata, outfile)