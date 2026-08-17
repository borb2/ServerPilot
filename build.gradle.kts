plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

val paperApiVersion = providers.gradleProperty("paperApiVersion").get()
val paperApiTarget = providers.gradleProperty("paperApiTarget").get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle(paperApiVersion)
    compileOnly("net.luckperms:api:5.5")
    testImplementation("io.papermc.paper:paper-api:$paperApiVersion")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-parameters"))
}

tasks.processResources {
    val tokens = mapOf(
        "version" to project.version.toString(),
        "apiVersion" to paperApiTarget
    )
    inputs.properties(tokens)
    filesMatching("paper-plugin.yml") {
        expand(tokens)
    }
}

tasks.test {
    useJUnitPlatform()
}
