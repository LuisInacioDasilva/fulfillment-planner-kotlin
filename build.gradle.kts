plugins {
    kotlin("jvm") version "2.4.21"
    application
}

repositories { mavenCentral() }

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin { jvmToolchain(21) }
application { mainClass.set("dev.luis.fulfillment.MainKt") }
tasks.test { useJUnitPlatform() }
