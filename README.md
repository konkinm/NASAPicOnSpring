# NASAPicOnSpring

Telegram Bot on JAVA Spring which send you pics and explanation text 
from NASA A Picture of A Day (APOD) using `https://apod.ellanan.com/api` [GitHub](https://github.com/ellanan/apod-api)

Deployed serverless on Yandex Cloud platform. [Try](https://t.me/NASAPic_bot)

### Supported commands:
- `/start`, `/help` - get bot description
- `/give` - get today's post
- `/random` - get random post

### Deployment

1. Install and configure yc CLI for your Yndex Cloud account.

2. Modify and run yc CLI command to create serverless function: `yc serverless function create --name=java-function`

3. Create `src.zip` archive with `src/` folder and `pom.xml` file.

4. Modify and run yc CLI command to create serverless function version
```
yc serverless function version create `
  --function-name=java-function `
  --runtime java17 `
  --entrypoint ru.konkin.telegram.NASAPicOnSpringBot.NASAPicOnSpringBotApplication `
  --memory 256m `
  --execution-timeout 15s `
  --environment "YA_API_TOKEN=Api-Key <your yandex API token>" ` 
  --environment BOT_TOKEN=<your telegram bot token> `
  --source-path ./src.zip
```

5. Create entry point for the function by API Gateway service using Control panel
 - In the management console, select the folder where you want to create an API gateway.
 - In the list of services, select API Gateway.
 - Click Create API gateway.
 - Enter a name for the API gateway in the Name field.
 - (Optional) In the Description field, enter a description for the API gateway.
 - In the Specification section, add the OpenAPI specification text:

```
   openapi: 3.0.0
info:
  title: NASAPicOnSpringBot API
  version: 1.0.0
servers:
- url: https://<apigw-id>.apigw.yandexcloud.net
paths:
  /callback:
    post:
      x-yc-apigateway-integration:
        type: cloud_functions
        function_id: <function-id>
      operationId: callback
```
   
- Configure additional API gateway settings if needed.
- Click Create.

6. Set proper webhook adress for entry point by performing GET request: `https://api.telegram.org/bot<telegram bot token>/setWebhook?url=<URL of ApiGW entry point>/callback`
   
