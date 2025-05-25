plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        compileOnly("com.google.guava:guava:${rootProject.properties["google_guava_version"]}-jre")
        compileOnly("org.projectlombok:lombok:${rootProject.properties["lombok_version"]}")
        annotationProcessor("org.projectlombok:lombok:${rootProject.properties["lombok_version"]}")
    }

    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
        mavenCentral()
        mavenLocal()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* Rtag */
        maven("https://jitpack.io")
        /* PlaceholderApi */
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        /* SkinsRestorer */
        maven("https://repo.codemc.io/repository/maven-snapshots/")
    }

    tasks.processResources {
        filteringCharset = "UTF-8"
        filesMatching(arrayListOf("plugin.yml")) {
            expand(
                Pair("projectVersion", rootProject.properties["projectVersion"]),
            )
        }
    }
}