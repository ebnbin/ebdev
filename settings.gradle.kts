fun devInclude(vararg names: String) {
    if (names.isEmpty()) return
    val localProperties = java.util.Properties().also {
        File(rootDir, "local.properties")
            .takeIf { file -> file.isFile }
            ?.let { file -> it.load(file.reader()) }
    }
    if (localProperties.getProperty("dev.publish") != "false") return
    names.forEach {
        include(":$it")
        project(":$it").projectDir = File("../$it/lib")
    }
}

include(":lib")

//*********************************************************************************************************************

devInclude("eb", "ebui")
