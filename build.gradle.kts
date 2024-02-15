plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
    id("legacy-looming") version "1.5-SNAPSHOT" // Version must be the same as fabric-loom's
    id("maven-publish")
    kotlin("jvm") version("1.9.22")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.minecraftforge.net/")
    maven("https://raw.githubusercontent.com/BleachDev/cursed-mappings/main/") // MCP mappings
}

val minecraftVersion = getString("minecraft_version")
val yarnBuild = getString("yarn_build")
val loaderVersion = getString("loader_version")
val fabricVersion = getString("fabric_version")

group = property("mod_group")?: "error"
version = property("mod_version")?: "error"

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(legacy.yarn(minecraftVersion, yarnBuild)) // MCP mappings
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Legacy-Fabric API
    modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:$fabricVersion")

    // Kotlin support
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.17+kotlin.1.9.22")

    implementation(kotlin("stdlib"))
}

java {

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}


tasks {

    jar {
        from("LICENSE") {

            rename { "${it}_${base.archivesName.get()}" }

        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
        repositories {
            mavenLocal()
        }
    }
}

fun getString(string: String) = property(string) as String