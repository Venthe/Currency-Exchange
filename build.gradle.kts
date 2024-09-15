plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.freefair.lombok") version "8.10"
    id("org.openapi.generator") version "7.8.0"
}

group = "eu.venthe.interview"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("application.${archiveExtension.get()}")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName = "java"
    configOptions = mapOf(
        "library" to "restclient",
        "openApiNullable" to "false"
    )

    inputSpec = "${project.rootDir}/contract/nbp.openapi.yaml"
    ignoreFileOverride = "${project.rootDir}/.openapi-generator-client.ignore"
    invokerPackage = "eu.venthe.nbp.invoker"
    modelPackage = "eu.venthe.nbp.model"
    apiPackage = "eu.venthe.nbp.api"
}

sourceSets {
    main {
        java {
            srcDir("${project.rootDir}/build/generate-resources/main/src/main/java")
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

// Due to Gradle dependency error when upgrading from Lombok 8.6 to 8.10
// Based on solution https://discuss.gradle.org/t/implicit-dependency-among-tasks-but-the-tasks-do-not-exist/46127
tasks.configureEach {
    if (name == "generateEffectiveLombokConfig") {
        mustRunAfter(tasks.openApiGenerate)
    }
}
