architectury {
    neoForge()
}

loom {
    runs {
        create("data") {
            data()
            programArgs("--all", "--mod", "golemoverhaul")
            programArgs("--output", project(":common").file("src/main/generated/resources").absolutePath)
            programArgs("--existing", project(":common").file("src/main/resources").absolutePath)
        }
    }
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentNeoForge"].extendsFrom(this)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }

    val neoforgeVersion: String by project
    val minecraftVersion: String by project
    val mekanismVersion: String by project
    val jeiVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)
    forgeRuntimeLibrary("com.eliotlash.mclib:mclib:20")
    forgeRuntimeLibrary("com.teamresourceful:bytecodecs:1.0.2")

//    modLocalRuntime(group = "mezz.jei", name = "jei-$minecraftVersion-neoforge", version = jeiVersion) {
//        isTransitive = false
//    }
}
