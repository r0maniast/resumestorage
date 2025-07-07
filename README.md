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

## Структура проекта

```
resumestorage/
├── Dockerfile
├── pom.xml
├── README.md
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ru/
│   │   │       └── webapp/
│   │   │           ├── Config.java
│   │   │           ├── exception/
│   │   │           │   ├── ExistStorageException.java
│   │   │           │   ├── NotExistStorageException.java
│   │   │           │   └── StorageException.java
│   │   │           ├── model/
│   │   │           │   ├── ContactType.java
│   │   │           │   ├── Link.java
│   │   │           │   ├── ListSection.java
│   │   │           │   ├── Organization.java
│   │   │           │   ├── OrganizationSection.java
│   │   │           │   ├── Resume.java
│   │   │           │   ├── Section.java
│   │   │           │   ├── SectionType.java
│   │   │           │   └── TextSection.java
│   │   │           ├── sql/
│   │   │           │   ├── ConnectionFactory.java
│   │   │           │   ├── ExceptionUtil.java
│   │   │           │   ├── SqlExecutor.java
│   │   │           │   ├── SqlHelper.java
│   │   │           │   └── SqlTransaction.java
│   │   │           ├── storage/
│   │   │           │   ├── AbstractArrayStorage.java
│   │   │           │   ├── AbstractStorage.java
│   │   │           │   ├── ArrayStorage.java
│   │   │           │   ├── FileStorage.java
│   │   │           │   ├── ListStorage.java
│   │   │           │   ├── MapResumeStorage.java
│   │   │           │   ├── MapUuidStorage.java
│   │   │           │   ├── PathStorage.java
│   │   │           │   ├── SortedArrayStorage.java
│   │   │           │   ├── SqlStorage.java
│   │   │           │   ├── Storage.java
│   │   │           │   └── serializer/
│   │   │           │       ├── DataStreamSerializer.java
│   │   │           │       ├── JsonStreamSerializer.java
│   │   │           │       ├── ObjectStreamSerializer.java
│   │   │           │       ├── StreamSerializer.java
│   │   │           │       └── XmlStreamSerializer.java
│   │   │           ├── util/
│   │   │           │   ├── DateUtil.java
│   │   │           │   ├── HtmlUtil.java
│   │   │           │   ├── JsonParser.java
│   │   │           │   ├── LocalDateJsonAdapter.java
│   │   │           │   ├── LocalDateXmlAdapter.java
│   │   │           │   ├── SectionJsonAdapter.java
│   │   │           │   └── XmlParser.java
│   │   │           └── web/
│   │   │               └── ResumeServlet.java
│   │   ├── resources/
│   │   │   ├── init_db.sql
│   │   │   ├── log4j2.xml
│   │   │   └── resumes.properties
│   │   └── webapp/
│   │       ├── css/
│   │       │   └── main.css
│   │       ├── img/
│   │       │   └── pencil.png
│   │       ├── index.jsp
│   │       └── WEB-INF/
│   │           ├── jsp/
│   │           │   ├── edit.jsp
│   │           │   ├── fragments/
│   │           │   │   ├── footer.jsp
│   │           │   │   └── header.jsp
│   │           │   ├── list.jsp
│   │           │   │   └── view.jsp
│   │           └── web.xml
│   └── test/
│       └── java/
│           └── ru/
│               └── webapp/
│                   └── storage/
│                       ├── AbstractArrayStorageTest.java
│                       ├── AbstractStorageTest.java
│                       ├── AllStorageTest.java
│                       ├── ArrayStorageTest.java
│                       ├── DataPathStorageTest.java
│                       ├── JsonPathStorageTest.java
│                       ├── ListStorageTest.java
│                       ├── MapResumeStorageTest.java
│                       ├── MapUuidStorageTest.java
│                       ├── ObjectFileStorageTest.java
│                       ├── ObjectPathStorageTest.java
│                       ├── SortedArrayStorageTest.java
│                       ├── SqlStorageTest.java
│                       └── XmlPathStorageTest.java
```

## Контакты

- Email: romankrivtsov7@gmail.com
- Telegram: [@romYUkd](https://t.me/romYUkd)