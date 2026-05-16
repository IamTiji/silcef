import java.nio.file.Files

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
}

fun getPlatformName(): String {
    var platform = System.getProperty("os.name").lowercase()
    platform =
        if (platform.startsWith("windows")) "windows"
        else if (platform.startsWith("mac")) "macos"
        else if (platform.startsWith("linux")) "linux"
        else throw IllegalArgumentException("Unsupported operating system: ${System.getProperty("os.name")}")

    val osArch = when (System.getProperty("os.arch").lowercase()) {
        "amd64", "x86_64" -> "x64"
        "aarch64", "arm64", "arm" -> "arm64"
        "x86", "i386", "i686" -> "x86"
        else -> throw IllegalArgumentException("Unsupported arch: ${System.getProperty("os.arch")}")
    }

    val platformMap = mapOf(
        ("windows" to "x64") to "windows64",
        ("windows" to "arm64") to "windowsarm64",
        ("windows" to "x86") to "windows32",

        ("macos" to "x64") to "macosx64",
        ("macos" to "arm64") to "macosarm64",

        ("linux" to "x64") to "linux64",
        ("linux" to "arm64") to "linuxarm64"
    )

    return platformMap[platform to osArch]?: throw IllegalArgumentException("Unsupported platform: $platform")
}

tasks.register("getCEF") {
    val cefVersion = "147.0.14+g76d2442+chromium-147.0.7727.138"
    val cefReleaseCDN = "https://cef-builds.spotifycdn.com/"

    val url = "${cefReleaseCDN}cef_binary_${cefVersion}_${getPlatformName()}.tar.bz2"
    val file = Files.createTempFile("cef", ".tar.bz2")
    ant.invokeMethod("get", mapOf("src" to url, "dest" to file, "verbose" to "true"))

    copy {
        from(tarTree(resources.bzip2(file))) {
            eachFile {
                path = path.substringAfter("/")
            }
        }
        into("./cef")
    }
    delete(file)
}

tasks.register<Exec>("genNatives") {
    doFirst {
        val folder = File("src/main/java/com/tiji/silcef/natives")
        if (!folder.exists()) return@doFirst
        folder.listFiles()?.forEach { file -> if (file.nameWithoutExtension != "package-info") file.delete() }
    }

    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    commandLine(if (isWindows) "powershell" else "bash")
    if (isWindows) {
        args(
            $$"""
                Get-Content -Path "src/main/headers/toGenerate" | ForEach-Object {
                    Write-Host "Processing $_"
                    jextract -l ":cef/Release/libcef.lib" `
                    --output "src/main/java/" `
                    -t "com.tiji.silcef.natives" `
                    -I "cef" `
                    $_
                }
            """.trimIndent()
        )
    } else {
        args("-c", $$"""
            while IFS= read -r file; do
                echo "Processing $file"
                jextract -l ":cef/Release/libcef.so" \
                --output "src/main/java/" \
                -t "com.tiji.silcef.natives" \
                "-I" "cef" \
                "$file"
            done < src/main/headers/toGenerate
        """.trimIndent())
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.release.set(targetJavaVersion)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")!!}" }
    }
}