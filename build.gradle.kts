plugins {
    kotlin("multiplatform") version "1.5.10"
    application
    kotlin("plugin.serialization") version "1.5.10"
}


group = "com.movpasd"
version = "1.0-SNAPSHOT"


val ktorVersion = "1.5.2"
val jvmTargetVersion = "1.8"
val kotlinReactVersion = "17.0.2-pre.206-kotlin-1.5.10"
val logbackVersion = "1.2.3"


repositories {
    jcenter()
    mavenCentral()
}

kotlin {


    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = jvmTargetVersion
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }

    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }


    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$kotlinReactVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$kotlinReactVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.21")
                implementation(npm("styled-components", "~5.2.1"))
            }
        }
        val jsTest by getting

    }


}

application {
    mainClass.set("MainServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    this.duplicatesStrategy = DuplicatesStrategy.WARN
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}


// This shouldn't be needed since the IntelliJ Idea multiplatform project has kindly provided the tasks.named<Copy>
// jvmProcessResources task which should pack the js into the jar already. If you enable both it'll give a
// duplicate error.

//tasks.getByName<Jar>("jvmJar") {
//
//    val taskName = if (project.hasProperty("isProduction")) {
//        "jsBrowserProductionWebpack"
//    } else {
//        "jsBrowserDevelopmentWebpack"
//    }
//
//    val webpackTask = tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>(taskName)
//    dependsOn(webpackTask) // make sure JS gets compiled first
//    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
//
//}