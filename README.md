# Currency Exchange REST API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)
![Apache Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, 
и совершать расчёт конвертации произвольных сумм из одной валюты в другую.

[Техническое задание проекта](https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/)

---

Содержание:
- [Как запустить](#как-запустить)
- [Описание API](#описание-api)

## Как запустить

1. Клонировать репозиторий:

```sh
git clone https://github.com/aglushkovsky/currency-exchange-service.git
```

2. Выполнить команду для сборки проекта:

```sh
mvn clean package
```
3. Скопировать скомпилированный WAR-файл из папки `target` в директорию `webapps` Tomcat:

```sh
cp target/currency-exchange.war $CATALINA_HOME/webapps/
```

4. Запустить Tomcat:

```sh
cd $CATALINA_HOME/bin
./startup.sh
```

5. Получить доступ к приложению по адресу:

```
http://localhost:8080/currency-exchange
```

## Описание API

### Валюты

### GET `/currencies`

Получение списка валют. Пример ответа:

```json
[
    {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },   
    {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    }
]
```

HTTP коды ответов:

- успех - 200;
- ошибка (например, база данных недоступна) - 500.

### GET `/currency/EUR`

Получение конкретной валюты. Пример ответа:

```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```

HTTP коды ответов:

- успех - 200;
- код валюты отсутствует в адресе - 400;
- валюта не найдена - 404;
- ошибка (например, база данных недоступна) - 500.

### POST `/currencies`

Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). 
Поля формы - `name`, `code`, `sign`. Пример ответа - JSON представление вставленной в базу записи, включая её ID:

```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```

HTTP коды ответов:

- успех - 201;
- отсутствует нужное поле формы - 400;
- валюта с таким кодом уже существует - 409;
- ошибка (например, база данных недоступна) - 500.

### Обменные курсы

### GET `/exchangeRates`

Получение списка всех обменных курсов. Пример ответа:

```json
[
    {
        "id": 0,
        "baseCurrency": {
            "id": 0,
            "name": "United States dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "rate": 0.99
    }
]
```

HTTP коды ответов:

- успех - 200;
- ошибка (например, база данных недоступна) - 500.

### GET `/exchangeRate/USDEUR`

Получение конкретного обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. 
Пример ответа:

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```

HTTP коды ответов:

- успех - 200;
- коды валют пары отсутствуют в адресе - 400;
- обменный курс для пары не найден - 404;
- ошибка (например, база данных недоступна) - 500.

### POST `/exchangeRates`

Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). 
Поля формы - `baseCurrencyCode`, `targetCurrencyCode`, `rate`. Пример полей формы:

- `baseCurrencyCode` - USD
- `targetCurrencyCode` - EUR
- `rate` - 0.99

Пример ответа - JSON представление вставленной в базу записи, включая её ID:

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```

HTTP коды ответов:

- успех - 201;
- отсутствует нужное поле формы - 400;
- валютная пара с таким кодом уже существует - 409;
- одна (или обе) валюта из валютной пары не существует в БД - 404;
- ошибка (например, база данных недоступна) - 500.

### PATCH `/exchangeRate/USDEUR`

Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. 
Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). Единственное поле формы - `rate`.

Пример ответа - JSON представление обновлённой записи в базе данных, включая её ID:

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```

HTTP коды ответов:

- успех - 200;
- отсутствует нужное поле формы - 400;
- валютная пара отсутствует в базе данных - 404;
- ошибка (например, база данных недоступна) - 500.

### Обмен валюты

### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Расчёт перевода определённого количества средств из одной валюты в другую. 
Пример запроса - GET `/exchange?from=USD&to=AUD&amount=10`.

Пример ответа:

```json
{
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A€"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
}
```

Предусмотрено вычисление курсов по трём сценариям через:
- прямой курс;
- обратный курс;
- кросс-курс.

---

Для всех запросов, в случае ошибки, ответ может выглядеть так:

```json
{
  "message": "Валюта не найдена"
}
```
Значение `message` зависит от того, какая именно ошибка произошла.