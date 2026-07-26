plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven ("https://repo.codemc.io/repository/creatorfromhell/")
    maven ("https://repo.extendedclip.com/releases/")
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.20")
    compileOnly("net.luckperms:api:5.5")
    compileOnly(files("libs/Orbit.jar"))
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:3.2.0")
    compileOnly("com.nexomc:nexo:1.26.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    jar {
        destinationDirectory.set(file("D:/Minecraft/servers/26.1.2/plugins"))
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version )
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
