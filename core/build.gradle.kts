val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    implementation(project(":api"))
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:${rootProject.properties["packetevents_version"]}")
    compileOnly("com.magicrealms:magiclib:${rootProject.properties["magic_lib_version"]}")
    compileOnly("net.skinsrestorer:skinsrestorer-api:${rootProject.properties["skinsrestorer_version"]}")
    compileOnly("com.saicone.rtag:rtag:${rootProject.properties["rtag_version"]}")
    compileOnly("com.saicone.rtag:rtag-item:${rootProject.properties["rtag_version"]}")
    compileOnly("me.clip:placeholderapi:${rootProject.properties["placeholder_version"]}")
}

repositories {
    /* SkinsRestorer */
    maven("https://oss.sonatype.org/content/groups/public/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

// val target = file("$rootDir/target")
val target = file("D:\\Minecraft\\Servers\\1.21.4\\Lobby\\plugins")
if ("core" == project.name) {
    tasks.shadowJar {
        destinationDirectory.set(target)
        archiveClassifier.set("")
        archiveFileName.set("${rootProject.name}-${projectVersion}.jar")
    }
}

publishing {
    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = projectGroup
            artifactId = projectArtifactId
            version = projectVersion
            from(components["shadow"])
        }
    }
}