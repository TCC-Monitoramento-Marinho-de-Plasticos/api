FROM eclipse-temurin:17-jdk

# Instala dependências do sistema e Python
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata && \
    apt-get install -y python3 python3-pip libgl1 libglib2.0-0 libsm6 libxrender1 libxext6 && \
    apt-get clean

# Instala bibliotecas Python necessárias
RUN pip install --no-cache-dir --break-system-packages \
    opencv-python \
    matplotlib \
    numpy \
    scikit-learn \
    scikit-image \
    tensorflow \
    pillow


# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR e os arquivos do modelo/script
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY modelo/ modelo/

# Expõe a porta da aplicação
EXPOSE 8081

# Comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
