# Money Transfer Service

## Installation
1. `mvn clean package`
2. `cd target`
3. `java -jar money-transfer-service-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Usage
| HTTP Method | Endpoint           | Body                                                     | Description |
| ------------|------------------- | -------------------------------------------------------- | -----------------|
| GET         | /accounts/{id}     |                                                          | returns account by provided id |
| GET         | /accounts          |                                                          | returns all accounts |
| POST        | /accounts          | `{"id" : "1", "balance" : "1"}`                          | creates account |
| PUT         | /accounts          | `{"id" : "1", "balance" : "1"}`                          | updates account|
| DELETE      | /accounts/{id}     |                                                          | deletes account by provided id|
| PUT         | /accounts/transfer | `{"senderId" : "1", "receiverId" : "2", "amount" : "1"}` | transfers money from one account to another|

## Using SwaggerUI
* Download [SwaggerUI](https://github.com/swagger-api/swagger-ui)
* Unzip it
* Open `dist` folder
* Open `index.html` and replace existing url with `http://localhost:8080/swagger.json`
* Open `index.html` in any browser
