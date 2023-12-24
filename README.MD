Task:
1. Взять за основу проект из лекции паттернов
2. Придерживаться GitFlow: master -> develop -> feature/fix
3. Написать CRUD для всех таблиц
4. Для метода .findAll() сделать пагинацию (по умолчанию 20 элементов на странице, если pagesize не задан)
5. Сделать GET метод, для генерации чека в формате pdf (если товара не существует, тогда генерируем ошибку)
6. Прикрутить возможность инициализации бд и наполнения её данными с помощью параметра в application.yml файле, т.е. чтобы при подъеме приложения,  приложение создавало схему, таблицы и наполняло таблицы данными
7. Фильтры
8. UI НЕ нужен

REST- запросы:
1) GET all players: http://localhost:8081/players?page=2&page-size=2

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

2) GET player by ID: http://localhost:8081/players/550e8400-e29b-41d4-a716-446655440000

{"name":"John","surname":"Doe","dateBirth":"1990-05-15","number":7}

3) GET получить pdf отчет: http://localhost:8081/players/550e8400-e29b-41d4-a716-446655440000/checks/pdf
