import com.palantir.javapoet.ClassName
import fr.kenlek.jpgen.api.ForeignUtils
import fr.kenlek.jpgen.generator.ElementFilter
import fr.kenlek.jpgen.generator.PathProvider
import fr.kenlek.jpgen.generator.TypeResolver
import fr.kenlek.jpgen.generator.data.FunctionDeclaration
import fr.kenlek.jpgen.generator.data.HeaderDeclaration
import fr.kenlek.jpgen.generator.data.LayoutsDeclaration
import fr.kenlek.jpgen.generator.data.NumericType
import fr.kenlek.jpgen.plugin.GenerationTask

plugins {
    `java-library`
    alias(libs.plugins.jpgen)
    alias(libs.plugins.mavenPublish)
}

group = "fr.kenlek"
version = "1.4.344.1"
description = "Automatically generated Vulkan bindings for Java"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(libs.jpgen.api)
}

val vulkanSource: Provider<File> = layout.buildDirectory.dir("Vulkan-Headers").map(Directory::getAsFile)

tasks.register("downloadVulkanHeaders") {
    group = "vulkan"

    outputs.dir(vulkanSource)

    doLast {
        val zipFile = temporaryDir.resolve("Vulkan-Headers.zip")
        uri("https://github.com/KhronosGroup/Vulkan-Headers/archive/refs/heads/main.zip").toURL().openStream().use { input ->
            zipFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        copy {
            from(zipTree(zipFile))
            into(vulkanSource)

            eachFile {
                relativePath = RelativePath(isDirectory, *relativePath.segments.drop(1).toTypedArray())
                includeEmptyDirs = false
            }
        }
    }
}

val generationTask = tasks.register<GenerationTask>("generateVulkanBindings") {
    group = "vulkan"
    dependsOn("downloadVulkanHeaders")

    val vulkanPackage = "fr.kenlek.vulkan"
    val vulkanGlobalInclude: java.nio.file.Path = vulkanSource.get().toPath().resolve("include")

    inputs.dir(vulkanGlobalInclude)

    clangArguments = listOf("-I", vulkanGlobalInclude.toString())
    header = vulkanGlobalInclude.resolve("vulkan/vulkan.h").toFile()

    val interfacePath = ClassName.get(vulkanPackage, "Vulkan")
    val handleGroups = listOf(
        VulkanBaseHandle(ClassName.get(vulkanPackage, "VkInstance"), interfacePath, "getInstanceProcAddr").let {
            it to listOf(
                VulkanChildHandle(ClassName.get(vulkanPackage, "VkPhysicalDevice"), it)
            )
        },
        VulkanBaseHandle(ClassName.get(vulkanPackage, "VkDevice"), interfacePath, "getDeviceProcAddr").let {
            it to listOf(
                VulkanChildHandle(ClassName.get(vulkanPackage, "VkQueue"), it),
                VulkanChildHandle(ClassName.get(vulkanPackage, "VkCommandBuffer"), it),
                VulkanChildHandle(ClassName.get(vulkanPackage, "VkExternalComputeQueueNV"), it)
            )
        }
    )
    val handles = handleGroups.flatMap { (base, children) -> children.plus(base) }
    doFirst {
        handles.forEach(VulkanHandle::reset)
    }

    elementFilter = ElementFilter.ofPrefix(vulkanGlobalInclude)
    pathProvider = PathProvider.of(vulkanPackage)
        .filter(ElementFilter.ofPrefix(vulkanGlobalInclude.resolve("vulkan")))
        .or(PathProvider.of("$vulkanPackage.video")
            .filter(ElementFilter.ofPrefix(vulkanGlobalInclude.resolve("vk_video"))))
        .or(PathProvider.TOP_LEVEL)
    typeResolver = handles.map(TypeResolver::declarationMatcher)
        .fold(TypeResolver.EMPTY, TypeResolver::or)
        .or(TypeResolver.declarationMatcher(ClassName.get(vulkanPackage, "VkBool32"), NumericType.BOOL32))
        .or(TypeResolver.NAMED_CALLBACKS)
        .or(TypeResolver.DEFAULT)

    layoutsClassName = ClassName.get(vulkanPackage, "Layouts")
    process {
        val functions = results().functions().map { function ->
            FunctionDeclaration(ForeignUtils.stripAPIName(function.name, "vk"), function.type, function.parameterInfos)
        }
        handles.forEach { handle ->
            functions.forEach { function ->
                if (function.type.parameterTypes.firstOrNull() == handle) {
                    handle.register(function)
                }
            }
        }

        include(handles)
        include(HeaderDeclaration(interfacePath, functions.minus(handles.asSequence().flatMap { handle ->
            if (handle is VulkanBaseHandle) {
                handle.functions().filter { function -> function.name != handle.symbolFunctionName }
            }
            else {
                handle.functions()
            }
        }.toSet())))
        include(results().typeDeclarations(vulkanPackage))
        include(LayoutsDeclaration(layoutsClassName, declarations()))
    }

    outputDirectory = jpgen.defaultOutputDirectory.dir("src")
}

sourceSets.main {
    java.srcDir(generationTask.map(GenerationTask::getOutputDirectory))
}

tasks.compileJava {
    dependsOn(generationTask)

    options.javaModuleVersion = project.version.toString()
    options.compilerArgs.addAll(listOf("-Xlint:all,-restricted", "-Werror"))
}

tasks.register<JavaExec>("simpleTest") {
    group = "vulkan"

    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "SimpleTest"

    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

mavenPublishing {
    coordinates(group.toString(), name, version.toString())
    pom {
        name = project.name
        description = project.description
        url = "https://github.com/Twiguinou/vk-auto-bindings"
        developers {
            developer {
                name = "kenlek"
                email = "akushiru@kenlek.fr"
                url = "https://github.com/Twiguinou"
            }
        }

        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            connection = "scm:git:git://github.com/Twiguinou/vk-auto-bindings.git"
            developerConnection = "scm:git:ssh://github.com:Twiguinou/vk-auto-bindings.git"
            url = "https://github.com/Twiguinou/vk-auto-bindings/tree/master"
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
