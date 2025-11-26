# Golem Overhaul

To add this library to your project, do the following:

Kotlin DSL:
```kotlin
repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    implementation("tech.alexnijjar.golemoverhaul:golemoverhaul-neoforge-$minecraftVersion:$golemOverhaulVersion")
}
```

Groovy DSL:
```groovy
repositories {
    maven { url "https://maven.teamresourceful.com/repository/maven-public/" }
}

dependencies {
    implementation "tech.alexnijjar.golemoverhaul:golemoverhaul-neoforge-$minecraftVersion:$golemOverhaulVersion"
}
```