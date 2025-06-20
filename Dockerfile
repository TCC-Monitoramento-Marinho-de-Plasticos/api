FROM openjdk:17-jdk-slim

# Instala dependências do sistema e o Python
RUN apt-get update && \
    apt-get install -y python3 python3-pip libgl1-mesa-glx libglib2.0-0 libsm6 libxrender1 libxext6 && \
    apt-get clean

# Instala bibliotecas Python necessárias
RUN pip install opencv-python matplotlib numpy scikit-learn scikit-image

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR e os arquivos do modelo/script
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY modelo/ modelo/

# Expõe a porta da aplicação
EXPOSE 8080

# Comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
