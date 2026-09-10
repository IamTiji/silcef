plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
    id("maven-publish")
    id("com.gradleup.shadow") version "9.6.1"
}

version = project.property("mod_version")!!

base {
    archivesName.set((project.property("archives_base_name")!! as String)
        .format(project.property("minecraft_version")))
}

loom {
    accessWidenerPath.set(file("src/main/resources/silcef.accesswidener"))
}

repositories {
    maven ("https://maven.parchmentmc.org")
}

tasks.register("downloadJcefJar") {
    val dest = file("jcef/jcef.jar")
    outputs.file(dest)
    doLast {
        dest.parentFile.mkdirs()
        uri("https://github.com/IamTiji/java-cef/releases/download/1.0-beta.1/jcef.jar").toURL()
            .openStream().use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
    }
}

val fatJarIncluded by configurations.creating
dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")!!}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.11:2025.12.20@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")!!}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")!!}")

    fatJarIncluded(files("jcef/jcef.jar"))
    compileOnly(files("jcef/jcef.jar"))
}

tasks.shadowJar {
    configurations.set(listOf(fatJarIncluded))
}

tasks.processResources {
    from("src/main/resources/fabric.mod.json") {
        expand("version" to project.version,
            "minecraft_version" to project.property("minecraft_version")!!)
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val targetJavaVersion = 23
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23

    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")!!}" }
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).tags(
        "apiNote:a:API Note:"
    )
}