plugins {
    java
    idea
    id("net.neoforged.moddev") version "2.0.72" // https://projects.neoforged.net/neoforged/ModDevGradle
    id("maven-publish")
}

val minecraftVersion: String by project
val neoforgeVersion: String by project
val modId: String by project
val version: String by project
val resourcefulLibVersion: String by project
val resourcefulConfigVersion: String by project
val geckolibVersion: String by project
val jeiVersion: String by project
val reiVersion: String by project

base {
    archivesName.set("$modId-neoforge-$minecraftVersion")
}

repositories {
    mavenLocal()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    implementation("com.teamresourceful.resourcefullib:resourcefullib-neoforge-1.21:$resourcefulLibVersion")
    implementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-neoforge-1.21:$resourcefulConfigVersion")
    implementation("software.bernie.geckolib:geckolib-neoforge-$minecraftVersion:$geckolibVersion")
    compileOnly("mezz.jei:jei-$minecraftVersion-neoforge-api:$jeiVersion")
    compileOnly("me.shedaniel:RoughlyEnoughItems-api-neoforge:$reiVersion")
    implementation("com.teamresourceful:bytecodecs:1.1.2")

//    runtimeOnly("me.shedaniel:RoughlyEnoughItems-neoforge:$reiVersion")
    runtimeOnly("mezz.jei:jei-$minecraftVersion-neoforge:$jeiVersion")
}

neoForge {
    version = neoforgeVersion
    validateAccessTransformers = true

    runs {
        register("client") {
            client()
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        }

        register("server") {
            server()
        }

        register("data") {
            data()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }

    mods.register(modId) {
        sourceSet(sourceSets.main.get())
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        exclude(".cache")

        val properties = mapOf(
            "minecraftVersion" to minecraftVersion,
            "neoforgeVersion" to neoforgeVersion.split(".")[0],
            "version" to version,
            "modId" to modId,
            "resourcefulLibVersion" to resourcefulLibVersion,
            "resourcefulConfigVersion" to resourcefulConfigVersion,
            "geckolibVersion" to geckolibVersion
        )

        inputs.properties(properties)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(properties)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "$modId-neoforge-$minecraftVersion"
            from(components["java"])

            pom {
                name.set("Golem Overhaul NeoForge")
                url.set("https://github.com/bonsaistudi0s/$modId")

                scm {
                    connection.set("git:https://github.com/bonsaistudi0s/$modId.git")
                    developerConnection.set("git:https://github.com/bonsaistudi0s/$modId.git")
                    url.set("https://github.com/bonsaistudi0s/$modId")
                }

                licenses {
                    license {
                        name.set("ARR")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/alexnijjar/")
            credentials {
                username = System.getenv("MAVEN_USER")
                password = System.getenv("MAVEN_PASS")
            }
        }
    }
}