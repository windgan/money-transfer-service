# AccountService

##Installation
1. `mvn clean package`
2. `cd target`
3. `java -jar money-transfer-service-1.0-SNAPSHOT-jar-with-dependencies.jar`

##Usage
* GET /accounts/{id}
  returns Account by provided id

* GET /accounts
  returns all accounts

* POST /accounts
   body sample: `{"id" : "1", "balance" : "1"}`
   creates Account

* PUT /accounts
  body sample: `{"id" : "1", "balance" : "1"}`
  updates Account

* DELETE /accounts/{id}
  deletes Account by provided id

* PUT /accounts/transfer
  body sample: `{"senderId" : "1", "receiverId" : "2", "amount" : "1"}`
  transfer money  from one account to another

##Using SwaggerUI
* Download [SwaggerUI](https://github.com/swagger-api/swagger-ui)
* Unzip it
* Open `dist` folder
* Open `index.html` and replace existing url with `http://localhost:8080/swagger.json`
* Open `index.html` in any browser