FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        tzdata \
        python3 \
        python3-pip \
        python3-venv \
        libgl1-mesa-glx \
        libglib2.0-0 \
        libsm6 \
        libxrender1 \
        libxext6 && \
    ln -fs /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir --break-system-packages \
    opencv-python \
    matplotlib \
    numpy \
    scikit-learn \
    scikit-image \
    tensorflow \
    pillow

WORKDIR /app

COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY modelo/ modelo/

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
