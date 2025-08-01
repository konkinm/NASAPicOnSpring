import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
}

group = "space.maxkonkin"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val springVersion = "5.3.30"

dependencies {
    // Spring
    implementation("org.springframework:spring-context:$springVersion")
    implementation("org.springframework:spring-webmvc:$springVersion")

    // TelegramBots
    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")

    // Snakeyaml
    implementation("org.yaml:snakeyaml:2.2")

    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.12")

    // YDB
    implementation("tech.ydb:ydb-sdk-table:2.1.7")
    implementation("tech.ydb.auth:yc-auth-provider:2.1.1")
    implementation("tech.ydb:ydb-auth-api:1.0.0")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.6.7") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
    }
    implementation("com.fasterxml.jackson.core:jackson-core:2.6.7")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.6.7") {
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
    }
    testImplementation("com.fasterxml.jackson.core:jackson-core:2.6.7")
    testImplementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
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
