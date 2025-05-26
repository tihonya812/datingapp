# Базовый образ с Java 23
FROM openjdk:23-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный jar-файл
COPY target/datingapp-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт, который будет использовать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]