import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   java
   `java-library`
   `maven-publish`
   signing
   kotlin("jvm") version "1.6.21"
}

group = "io.kotest.extensions"
version = Ci.version

dependencies {
   // Kotlin
   implementation(kotlin("reflect"))

   // Kotest
   implementation(libs.kotest.framework.api)
   testImplementation(libs.kotest.runner.junit5)

   // Gherkin
   implementation(libs.gherkin)
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   testLogging {
      showExceptions = true
      showStandardStreams = true
      exceptionFormat = TestExceptionFormat.FULL
   }
}

tasks.withType<KotlinCompile> {
   kotlinOptions.jvmTarget = "1.8"
}

repositories {
   mavenLocal()
   mavenCentral()
   maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots")
   }
}

apply("./publish.gradle.kts")
