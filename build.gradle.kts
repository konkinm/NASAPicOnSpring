import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
}

group = "space.maxkonkin"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val telegramBotsVersion = "9.0.0"
val jacksonVersion = "2.6.7"
val jacksonAnnotationsVersion = "2.16.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    // https://mvnrepository.com/artifact/io.insert-koin/koin-core
    implementation("io.insert-koin:koin-core:3.5.6")

    // TelegramBots
    implementation("org.telegram:telegrambots-webhook:$telegramBotsVersion")
    implementation("org.telegram:telegrambots-client:$telegramBotsVersion")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")

    // Snakeyaml
    implementation("org.yaml:snakeyaml:2.2")

    // Logger
    implementation("ch.qos.logback:logback-classic:1.5.13")

    // YDB
    implementation("tech.ydb:ydb-sdk-table:2.1.7")
    implementation("tech.ydb.auth:yc-auth-provider:2.1.1")
    implementation("tech.ydb:ydb-auth-api:1.0.0")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
    }
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
    }
    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    jvmArgs("-Dfile.encoding=UTF-8")
    useJUnitPlatform()
}
