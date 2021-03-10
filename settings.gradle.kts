include(":lib")

fun devInclude(id: String) {
    if (java.util.Properties().also { it.load(File(rootDir, "local.properties").reader()) }["dev.publish"] == "false") {
        include(":$id")
        project(":$id").projectDir = File("../$id/lib")
    }
}

//*********************************************************************************************************************

devInclude("eb")
devInclude("ebui")
