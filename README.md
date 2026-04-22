# NASAPicOnSpring

Telegram Bot on Kotlin (Koin DI) which send you pics and explanation text
from NASA A Picture of A Day (APOD) using `https://apod.ellanan.com/api` [GitHub](https://github.com/ellanan/apod-api)

Deployed serverless on Yandex Cloud platform. [Try](https://t.me/NASAPic_bot)

### Supported commands:
- `/start`, `/help` - get bot description
- `/today` - get today's post
- `/random` - get random post
- `/schedule` - toggle daily scheduled posts
- `/translate` - toggle EN→RU translation
- `YYYY-MM-DD` - get post for a specific date (no earlier than 1995-06-20)

### Deployment

1. Install and configure yc CLI for your Yandex Cloud account.

2. Modify and run yc CLI command to create serverless functions:
   ```
   yc serverless function create --name=<function_name>
   ```
   Repeat for the timer function used by `/schedule`.

3. Create `src_kotlin_prod.zip` archive with `src/`, `build.gradle.kts`, and `settings.gradle.kts`:
   ```
   tar.exe -a -c -f src_kotlin_prod.zip src build.gradle.kts settings.gradle.kts
   ```

4. Modify and run yc CLI command to create the main serverless function version:
```
yc serverless function version create ^
  --service-account-id <service_account_id> ^
  --function-id <function_id> ^
  --runtime kotlin20 ^
  --entrypoint space.maxkonkin.nasapicbot.web.Handler ^
  --memory 2048m ^
  --execution-timeout 15s ^
  --environment "YA_API_TOKEN=Api-Key <your yandex API token>" ^
  --environment BOT_TOKEN=<your telegram bot token> ^
  --environment "DATABASE=<your ydb database path>" ^
  --environment PROFILE=prod ^
  --source-path ./src_kotlin_prod.zip
```

5. Deploy the timer function version (used for scheduled posts):
```
yc serverless function version create ^
  --service-account-id <service_account_id> ^
  --function-id <timer_function_id> ^
  --runtime kotlin20 ^
  --entrypoint space.maxkonkin.nasapicbot.web.TimerHandler ^
  --memory 2048m ^
  --execution-timeout 15s ^
  --environment "YA_API_TOKEN=Api-Key <your yandex API token>" ^
  --environment BOT_TOKEN=<your telegram bot token> ^
  --environment "DATABASE=<your ydb database path>" ^
  --environment PROFILE=prod ^
  --source-path ./src_kotlin_prod.zip
```

6. Create a new message queue:
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

7. Modify and run yc CLI command to create trigger which automatically sends messages from queue to function:

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

8. Create entry point for the queue by API Gateway service using Control panel:
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
- url: <apigw_url>
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

9. Set proper webhook address for entry point by performing GET request: `https://api.telegram.org/bot<telegram bot token>/setWebhook?url=<URL of ApiGW entry point>/callback`
