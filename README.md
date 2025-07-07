# ResumeStorage

Учебное веб-приложение для хранения и управления резюме.

## Автор

Кривцов Роман

## Демо

Приложение развернуто и доступно по адресу:  
[https://resumestorage.onrender.com](https://resumestorage.onrender.com)

## Технологии

- Java
- JSP/Servlets
- PostgreSQL
- Maven
- JUnit5
- Log4j2 with SLF4J
- Docker

## Возможности

- Добавление, редактирование, удаление и просмотр резюме
- Хранение данных в удалённой базе данных PostgreSQL
- Веб-интерфейс для управления резюме

## Как запустить локально через Docker

1. Клонируйте репозиторий:
   ```sh
   git clone https://github.com/yourusername/resumestorage.git
   cd resumestorage
   ```
2. Создайте файл `.env` в корне проекта со следующими переменными:
   ```
   db.url=jdbc:postgresql://dpg-d1ig9nemcj7s738s84o0-a.frankfurt-postgres.render.com:5432/resumestorage
   db.user=resumestorage_user
   db.password=xGmcm6gjWnzsNts4n6e5lGUk1zJmEwS1
   ```
3. Соберите и запустите контейнер:
   ```sh
   docker build -t resumestorage .
   docker run --env-file .env -p 8080:8080 resumestorage
   ```
4. Приложение будет доступно по адресу [http://localhost:8080](http://localhost:8080)

## Контакты

- Email: romankrivtsov7@gmail.com
- Telegram: [@romYUkd](https://t.me/romYUkd)