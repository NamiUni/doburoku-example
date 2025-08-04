import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-rc3"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "io.github.namiuni"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("io.github.miniplaceholders:miniplaceholders-api:2.3.0")
    implementation("io.github.namiuni:doburoku-reflect-minimessage:1.0.0-SNAPSHOT")
}

paperPluginYaml {
    author = "Namiu (うにたろう)"
    apiVersion = "1.21"
    version = "1.0-SNAPSHOT"
    main = "io.github.namiuni.doburoku.example.DoburokuExample"
    name = "DoburokuExample"

    dependencies {
        server("MiniPlaceholders", Load.BEFORE, false)
    }
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
        this.archiveVersion.set(paperPluginYaml.version)
    }

    runServer {
        minecraftVersion("1.21.5")
        downloadPlugins {
            modrinth("miniplaceholders", "wck4v0R0")
            modrinth("player-expansion", "fFVyTdRI")
        }
    }
}
