import pickle
import cv2
import matplotlib.pyplot as plt
import numpy as np
import sys
from skimage.feature import hog

def extract_hog_features(img):
    return hog(
        img,
        orientations=9,
        pixels_per_cell=(8, 8),
        cells_per_block=(2, 2),
        block_norm='L2-Hys',
        feature_vector=True
    )

def main():
    if len(sys.argv) < 3:
        print("Erro: caminhos da imagem e do modelo não foram fornecidos")
        sys.exit(1)

    image_path = sys.argv[1]
    model_path = sys.argv[2]
    output_image_path = "/tmp/output_prediction.png"

    try:
        model = pickle.load(open(model_path, "rb"))
    except Exception as e:
        print(f"Erro ao carregar o modelo: {e}")
        sys.exit(1)

    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if img is None:
        print("Erro: imagem inválida")
        sys.exit(1)

    img = cv2.resize(img, (256, 256)).astype("float32") / 255.0

    hog_features = extract_hog_features(img)
    prediction = model.predict([hog_features])[0]

    label = "SEM lixo" if prediction == 0 else "COM lixo"

    plt.imshow(img, cmap='gray')
    plt.title("Predição: " + label)
    plt.axis('off')
    plt.savefig(output_image_path)
    print(f"{output_image_path}|{label}")

if __name__ == "__main__":
    main()
