import com.github.gradle.node.NodeExtension
import com.github.gradle.node.task.NodeSetupTask
import com.github.gradle.node.variant.VariantComputer
import com.github.gradle.node.yarn.task.YarnSetupTask
import com.github.gradle.node.yarn.task.YarnTask
import org.gradle.internal.extensions.core.serviceOf
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download = true
    version = "24.13.0"
    yarnVersion = "3.2.8"
    workDir = file("${project.projectDir}/.gradle/nodejs")
    yarnWorkDir = file("${project.projectDir}/.gradle/yarn")
}


tasks.withType(NodeSetupTask::class.java).configureEach {
    doLast {
        val nodeExtension = NodeExtension[project]
        val variantComputer = VariantComputer()

        val isWindows = nodeExtension.resolvedPlatform.get().isWindows()

        // fix corepack symbolicLink
        fun computeCorepackScriptFile(nodeDirProvider: Provider<Directory>): Provider<String> {
            return nodeDirProvider.map { nodeDir ->
                if (isWindows) nodeDir.dir("node_modules/corepack/dist/corepack.js").asFile.path
                else nodeDir.dir("lib/node_modules/corepack/dist/corepack.js").asFile.path
            }
        }

        val nodeDirProvider = nodeExtension.resolvedNodeDir
        val nodeBinDirProvider = variantComputer.computeNodeBinDir(nodeDirProvider, nodeExtension.resolvedPlatform)
        val nodeBinDirPath = nodeBinDirProvider.get().asFile.toPath()
        val corepackScript = nodeBinDirPath.resolve("corepack")
        val scriptFile =
            computeCorepackScriptFile(nodeDirProvider)
        if (Files.deleteIfExists(corepackScript)) {
            Files.createSymbolicLink(
                corepackScript,
                nodeBinDirPath.relativize(Paths.get(scriptFile.get()))
            )
        }

        val yarnDir = variantComputer.computeYarnDir(nodeExtension).get()
        val dirPath = if (isWindows) yarnDir else yarnDir.dir("bin")

        val nodeExecutable = nodeBinDirPath.resolve("node")
        mkdir(dirPath)
        serviceOf<ExecOperations>().exec {
            // actually YarnSetup execute here
            commandLine(nodeExecutable, corepackScript, "enable", "--install-directory", dirPath)
        }

    }
}

tasks.withType(YarnSetupTask::class.java).configureEach {
    enabled = false
}

tasks.register<YarnTask>("yarnBuild") {
    group = "build"
    dependsOn(tasks.yarn)
    args = listOf("run", "build")
}

tasks.register<YarnTask>("yarnTest") {
    group = "verification"
    dependsOn(tasks.yarn)
    args = listOf("test")
}