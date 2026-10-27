plugins {
    id("io.papermc.paperweight.userdev") version "1.7.7"
    kotlin("jvm") version "2.1.0"
}

group = "ru.darkrpmc"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public")
}

val exposedVersion: String by project
dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
}

kotlin { jvmToolchain(21) }

tasks.register<Copy>("copyJar") {
    val jarTask = tasks.named<Jar>("jar")
    from(jarTask)
    into("..\\..\\..\\servers\\LobbyServer\\plugins")
}

tasks.jar { finalizedBy("copyJar") }