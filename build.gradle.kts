import sh.miles.pineapplekit.data.PineappleModule

plugins {
    id("idea")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("sh.miles.pineapplekit") version "1.0.0-SNAPSHOT"
}

group = "sh.miles"
version = "1.0.0-SNAPSHOT"

pineappleKit {
    spigotVersion = "1.20.4"
    mainPackage = "${project.group}.${project.name.lowercase()}"
    modules = listOf(
        PineappleModule.BUNDLE.withExclusionPattern(
            listOf(
                "sh.miles.pineapple.nms.impl.v1_20_R4",
            )
        ),
        PineappleModule.TILES
    )
}

dependencies {
    bukkitLibrary(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(17)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

bukkit {
    name = "PineappleEnvoys"
    version = project.version.toString()
    main = "sh.miles.${project.name.lowercase()}.${project.name}Plugin"
    depend = listOf("Vault")
    softDepend = listOf("EconomyShopGUI", "EconomyShopGUI-Premium")
    apiVersion = "1.20" // LATEST
}
