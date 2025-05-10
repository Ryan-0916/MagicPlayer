rootProject.name = "MagicPlayer"
include(":core")
include(":setting")
include(":velocity")
include(":common")
include(":api")
pluginManagement {
    repositories {
        gradlePluginPortal()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* PacketEvents */
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        /* Rtag */
        maven("https://jitpack.io")
        /* PlaceholderApi */
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

