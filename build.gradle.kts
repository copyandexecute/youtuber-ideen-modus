import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

val mcVersion = "1.18.2"

group = "de.hglabor"
version = "${mcVersion}_v1"

repositories {
    mavenCentral()
}

dependencies {
    // PaperMC Dependency
    paperDevBundle("$mcVersion-R0.1-SNAPSHOT")

    // KSpigot dependency
    implementation("net.axay:kspigot:1.18.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    build {
        dependsOn(reobfJar)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

bukkit {
    name = "youtuber-ideen-modus"
    apiVersion = "1.18"
    authors = listOf(
        "NoRiskk",
    )
    main = "$group.youtuberideen.YoutuberIdeen"
    version = getVersion().toString()
    libraries = listOf(
        "net.axay:kspigot:1.18.2",
    )
}
