import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

import tensorflow as tf
import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import sys

def main():
    if len(sys.argv) < 3:
        print("Erro: caminhos da imagem e do modelo não foram fornecidos", file=sys.stderr)
        sys.exit(1)

    img_path = sys.argv[1]
    model_path = sys.argv[2]
    output_image_path = "/tmp/output_prediction.png"

    try:
        model = load_model(model_path)
    except Exception as e:
        print(f"Erro ao carregar o modelo: {e}", file=sys.stderr)
        sys.exit(1)

    try:
        img = image.load_img(img_path, target_size=(150, 150))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0) / 255.0
    except Exception as e:
        print(f"Erro ao processar a imagem: {e}", file=sys.stderr)
        sys.exit(1)

    predicao = model.predict(img_array, verbose=0)
    classe_prevista = "SEM lixo" if predicao[0][0] < 0.5 else "COM lixo"

    # Apenas gerar a saída esperada
    print(f"{output_image_path}|{classe_prevista}")

if __name__ == "__main__":
    main()
