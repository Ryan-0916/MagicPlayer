rootProject.name = "MagicPlayer"
include(":common")
include(":api")

include(":core")
include(":velocity")
pluginManagement {
    repositories {
        gradlePluginPortal()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* SkinsRestorer */
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        /* Rtag */
        maven("https://jitpack.io")
        /* PlaceholderApi */
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

