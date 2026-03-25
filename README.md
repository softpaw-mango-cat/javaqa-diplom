## Как запускать автотесты к этому проекту

### Что понадобится: 
- IDE Intellij Idea
- Docker Desktop
- Браузер Google Chrome

### Процесс запуска
- Открыть проект в Intellij Idea
- Открыть приложение Docker Desktop
- Открыть терминал в Idea и выполнить команду `docker-compose up`
- Подождать, пока скачаются все нужные образы


**Проверка работы контейнеров**: 

В Docker Desktop отображаются 3 контейнера - `postgres`, `mysql` и `javaqa-diplom-node-app`

![img.png](files/img.png)


В терминале ввести команду `docker-compose ps`, должны также отобразиться 3 контейнера

![img_1.png](files/img_1.png)


#### Запуск приложения с поддержкой MySQL
- В файле `application.properties` проверить, что строка с подключением к mysql откомментирована, как на скриншоте

![img_2.png](files/img_2.png)


- В терминале ввести команду:
  `java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar app/aqa-shop.jar`
- Проверить, открывается ли приложение в браузере на http://localhost:8080/
#### Запуск приложения с поддержкой Postgre
- В файле `application.properties` проверить, что строка с подключением к postgre откомментирована, как на скриншоте

![img_3.png](files/img_3.png)


- В терминале ввести команду:
  `java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" "-Dspring.datasource.username=app" "-Dspring.datasource.password=pass" -jar app/aqa-shop.jar`
- Проверить, открывается ли приложение в браузере на http://localhost:8080/


- Запустить автотесты из классов через интерфейс Idea - `PaymentUITest`, `CreditUITest`