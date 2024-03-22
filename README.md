# NASAPicOnSpring

Telegram Bot on JAVA Spring which send you pics and explanation text 
from NASA A Picture of A Day (APOD) using `https://apod.ellanan.com/api` [GitHub](https://github.com/ellanan/apod-api)

Deployed serverless on Yandex Cloud platform. [Try](https://t.me/NASAPic_bot)

### Supported commands:
- `/start`, `/help` - get bot description
- `/give` - get today's post
- `/random` - get random post

### Deployment

1. Install and configure yc CLI for your Yandex Cloud account.

2. Modify and run yc CLI command to create serverless function: `yc serverless function create --name=java-function`

3. Create `src.zip` archive with `src/` folder and `pom.xml` file.

4. Modify and run yc CLI command to create serverless function version
```
yc serverless function version create `
  --service-account-id <service_account_id> `
  --function-name=java-function `
  --runtime java21 `
  --entrypoint space.maxkonkin.nasapicbot.web.Handler `
  --memory 256m `
  --execution-timeout 10s `
  --environment "YA_API_TOKEN=Api-Key <your yandex API token>" ` 
  --environment BOT_TOKEN=<your telegram bot token> `
  --source-path ./src.zip
```

5. Create a new message queue
 - Install and configure the AWS CLI.
 - Run the following command in the terminal:
   `aws sqs create-queue \
   --queue-name <queue_name> \
   --endpoint <endpoint>`
  Where:
  --queue-name: Name of the new queue, e.g., sample-queue.
  --endpoint: Endpoint in the https://message-queue.api.cloud.yandex.net/ value.
   Result: `{
               "QueueUrl": "<queue_url>"
            }`

6. Modify and run yc CLI command to create trigger which automatically sends messages from queue to function:

```
yc serverless trigger create message-queue \
   --name <trigger_name> \
   --queue <queue_ID> \
   --queue-service-account-id <service_account_ID> \
   --invoke-function-id <function_ID> \
   --invoke-function-service-account-id <service_account_ID> \
   --batch-size 1 \
   --batch-cutoff 10s
```

6. Create entry point for the queue by API Gateway service using Control panel
 - In the management console, select the folder where you want to create an API gateway.
 - In the list of services, select API Gateway.
 - Click Create API gateway.
 - Enter a name for the API gateway in the Name field.
 - (Optional) In the Description field, enter a description for the API gateway.
 - In the Specification section, add the OpenAPI specification text:

```
info:
  title: Bot API
  version: 1.0.0
servers:
- url: https://d5dob1n2uv2ss0qvgb76.apigw.yandexcloud.net
paths:

  /callback:
    post:
      x-yc-apigateway-integration:
        type: cloud_ymq
        action: SendMessage
        queue_url: <queue_url>
        folder_id: <folder_id>
        service_account_id: <bot_service_account_id>
```

  - Configure additional API gateway settings if needed.
  - Click Create.

7. Set proper webhook adress for entry point by performing GET request: `https://api.telegram.org/bot<telegram bot token>/setWebhook?url=<URL of ApiGW entry point>/callback`
