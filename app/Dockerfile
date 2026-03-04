# Используем образ с Java
FROM amazoncorretto:11-alpine

# Указываем, что приложение будет доступно на порту 8080
EXPOSE 8080

# Копируем jar-файл в контейнер
COPY aqa-shop.jar /app/app.jar

# Устанавливаем рабочую директорию
WORKDIR /app

# Команда для запуска приложения
# Переменные окружения для параметров подключения
CMD java -jar app.jar \
    --spring.datasource.url=${DB_URL} \
    --spring.datasource.username=${DB_USER} \
    --spring.datasource.password=${DB_PASSWORD} \
    --spring.credit-gate.url=${SPRING_CREDIT_GATE_URL} \
    --spring.payment-gate.url=${SPRING_PAYMENT_GATE_URL}