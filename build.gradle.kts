plugins {
    idea apply true
    id("org.jetbrains.intellij") version "0.4.21"
    java
    kotlin("jvm") version "1.3.72"
}

apply {
    plugin("java")
    plugin("kotlin")
    plugin("org.jetbrains.intellij")
}

group = "info.galliano.idea.pionPlugin"
version = project.property("version").toString()

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = project.property("ideaVersion").toString()
    setPlugins(
        "com.jetbrains.php:${project.property("phpPluginVersion").toString()}",
        "java",
        "java-i18n",
        "PsiViewer:3.28.93",
        "properties"
    )
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    patchPluginXml {
        changeNotes(project.property("changeNotes").toString().replace("\n", "<br>\n"))
    }
}