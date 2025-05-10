val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
    compileOnly("com.magicrealms:magiclib:1.0")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.6.2")
    compileOnly("com.saicone.rtag:rtag:1.5.9")
    compileOnly("com.saicone.rtag:rtag-item:1.5.9")
    compileOnly("me.clip:placeholderapi:2.11.6")
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