# Электронные протоколы бриджа

## Запуск

Нужны Docker и Compose. Из папки проекта:

```sh
cp .env.example .env
docker compose up --build -d
```

API: `http://localhost:8080/api`.
[Swagger UI](http://localhost:8080/api/swagger-ui/index.html).
Порты: приложение — 8080, PostgreSQL — 5432.
Остановка с сохранением данных: `docker compose down`.

Для запуска из IDE нужен JDK 25: поднять БД командой
`docker compose up -d postgres`, затем запустить `./gradlew bootRun`.

## Ручки

Все пути относительно `/api`.

| Метод | Путь | Результат |
| --- | --- | --- |
| POST | `/users` | Создать пользователя, вернуть UUID и `201` |
| GET | `/users` | Все пользователи |
| GET | `/users/{userId}` | Пользователь по UUID |
| GET | `/users/by-surname?surname=Иванов` | Поиск по точной фамилии |
| GET | `/users/by-username?username=player` | Поиск по точному username |
| POST | `/boards` | Создать сдачу, вернуть ID и `201` |
| GET | `/boards` | Все сдачи |
| GET | `/boards/{boardId}` | Сдача по ID |
| POST | `/boards/{boardId}/entries/{userId}/mp` | Записать результат и рассчитать MP |
| POST | `/boards/{boardId}/entries/{userId}/imps` | Записать результат и рассчитать IMP |
| GET | `/boards/{boardId}/entries` | Протокол сдачи |

Тело создания пользователя: `{"username":"player","name":"Иван","surname":"Иванов"}`.
Создание сдачи: `{"number":1}`.
Запись результата: `{"contract":"4S","declarer":"N","lead":"cK","result":1}`.
`result` — отклонение от контракта; распасовка — `{"contract":"pass","result":0}`.
Повторная запись того же автора исправляет его результат.
У `/imps` есть необязательный параметр `throwAwayPercent` от 0 до 0.5
(не включая 0.5), по умолчанию 0.
