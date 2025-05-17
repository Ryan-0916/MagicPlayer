dependencies {
    implementation(project(":common"))
    compileOnly("io.papermc.paper:paper-api:${rootProject.properties["paper_version"]}-R0.1-SNAPSHOT")
    compileOnly("com.magicrealms:magiclib:${rootProject.properties["magic_lib_version"]}")
    compileOnly("net.skinsrestorer:skinsrestorer-api:${rootProject.properties["skinsrestorer_version"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    /* SkinsRestorer */
    maven("https://oss.sonatype.org/content/groups/public/")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}