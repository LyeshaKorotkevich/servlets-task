Task:
1. Взять за основу проект из лекции сервлетов
2. Придерживаться GitFlow: master -> develop -> feature/fix
3. Перевести проект на Spring 6.1.2  

## Инструкция по запуску проекта
* Клонировать репозиторий себе на компьютер.
* Собрать проект с помощью Gradle
  ```bash
  gradle build
  ```
* Поднять БД с помощью docker-compose
  ```bash
  docker-compose up
  ```
* Запустить tomcat server

REST- запросы:
1) GET all players: http://localhost:8081/players?page=2&page-size=2

```json
[
  {
    "name": "Bob",
    "surname": "Johnson",
    "dateBirth": "1988-09-10",
    "number": 14
  },
  {
    "name": "Emily",
    "surname": "Brown",
    "dateBirth": "1992-03-20",
    "number": 3
  }
]
```

2) GET player by ID: http://localhost:8081/players/550e8400-e29b-41d4-a716-446655440000

```json 
{"name":"John","surname":"Doe","dateBirth":"1990-05-15","number":7} 
```

3) GET получить pdf отчет: http://localhost:8081/players/550e8400-e29b-41d4-a716-446655440000/checks/pdf
4) POST: http://localhost:8081/players
```json
{
    "name": "Leo",
    "surname": "Messi",
    "dateBirth": "1980-01-01",
    "number": 22
}
```

Response:
```json
{
    "name": "Leo",
    "surname": "Messi",
    "dateBirth": "1980-01-01",
    "number": 22
}
```
5) PUT: http://localhost:8081/players/15a6c3fb-2336-4ace-8f36-300b356d891f
```json
{
    "name": "Leonel",
    "surname": "Messi",
    "dateBirth": "1980-10-10",
    "number": 30
}
```

Response:
```json
{
    "name": "Leonel",
    "surname": "Messi",
    "dateBirth": "1980-10-10",
    "number": 30
}
```
6) Delete: http://localhost:8081/players/15a6c3fb-2336-4ace-8f36-300b356d891f  
   Response: Status 204
