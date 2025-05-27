FROM openjdk:17-jdk

# Instala JavaFX
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://download2.gluonhq.com/openjfx/17.0.12/openjfx-17.0.12_linux-x64_bin-sdk.zip && \
    unzip openjfx-17.0.12_linux-x64_bin-sdk.zip -d /opt && \
    rm openjfx-17.0.12_linux-x64_bin-sdk.zip

ENV PATH="/opt/javafx-sdk-17.0.12/bin:${PATH}"
ENV JAVAFX_HOME="/opt/javafx-sdk-17.0.12"

WORKDIR /app
COPY build/libs/tu-app.jar app.jar

CMD ["java", "--module-path", "/opt/javafx-sdk-17.0.12/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]