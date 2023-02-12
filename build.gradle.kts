/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.56"
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
}

taboolib {
    install("common")
    install("common-5")
    install("platform-velocity")

    classifier = null
    version = "6.0.10-73"

    description {
        contributors {
            name("xiaozhangup")
        }
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))

    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}