plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version")!!

base {
    archivesName.set(project.property("archives_base_name")!! as String)
}

loom {
    accessWidenerPath.set(file("src/main/resources/slicef.accesswidener"))
}

repositories {
    maven ("https://maven.parchmentmc.org")
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")!!}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.11:2025.12.20@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")!!}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")!!}")

    implementation(files("jcef/jcef.jar"))
}

tasks.processResources {
    from("src/main/resources/fabric.mod.json") {
        expand("version" to project.version,
            "minecraft_version" to project.property("minecraft_version")!!)
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val targetJavaVersion = 21
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()

    //toolchain {
    //    vendor.set(JvmVendorSpec.JETBRAINS)
    //    languageVersion.set(JavaLanguageVersion.of(25))
    //}
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")!!}" }
    }
}