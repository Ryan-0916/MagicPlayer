val projectVersion : String by project
val projectGroup : String by project
val projectArtifactId : String by project

dependencies {
    implementation(project(":common"))
    compileOnly("com.magicrealms:magiclib-velocity:${rootProject.properties["magic_lib_velocity_version"]}")
    compileOnly("com.velocitypowered:velocity-api:${rootProject.properties["velocity_version"]}-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:${rootProject.properties["velocity_version"]}-SNAPSHOT")
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

artifacts {
    archives(tasks.shadowJar)
}

tasks {
    shadowJar {
        archiveClassifier = ""
        archiveFileName = "${rootProject.name}-velocity-${projectVersion}.jar"
        destinationDirectory.set(file("$rootDir/target"))
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
            artifactId = "${projectArtifactId}-velocity"
            version = projectVersion
            from(components["shadow"])
        }
    }
}