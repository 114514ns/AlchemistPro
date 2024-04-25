import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.0-Beta"
    kotlin("plugin.lombok") version "1.8.22"
    id("io.freefair.lombok") version "5.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {

    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    implementation("cn.hutool:hutool-all:5.8.19")
    compileOnly ("org.projectlombok:lombok:1.18.28")
    testImplementation("org.junit.jupiter:junit-jupiter")
    annotationProcessor ("org.projectlombok:lombok:1.18.28")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("junit:junit:4.13.2")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    implementation("com.alibaba.fastjson2:fastjson2:2.0.33")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}