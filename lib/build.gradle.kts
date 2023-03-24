import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
  `java-library`
}

dependencies {
  implementation(kotlin("reflect"))
  implementation(libs.arrowCore)
  implementation(libs.arrowFxCoroutines)

  testImplementation(project(":testing-lib"))
  testImplementation(libs.junitApi)
  testImplementation(libs.kotestAssertions)
  testImplementation(libs.kotestAssertionsArrow)
  testImplementation(libs.kotestFrameworkApi)
  testImplementation(libs.kotestJunitRunnerJvm)
  testImplementation(libs.kotestProperty)
  testImplementation(libs.kotestPropertyArrow)

  testRuntimeOnly(libs.junitEngine)

  apply(plugin = libs.plugins.dokka.get().pluginId)
}

// Copies Quiver logo into Dokka output directory, making images accessible in documentation
tasks.register<Copy>("copyDocumentationImages") {
  from("../images/quiver-logo-01.svg")
  into("${getRootDir()}/lib/build/dokka/html/doc-images")
}

tasks.withType<DokkaTask>().configureEach {
  dependsOn("copyDocumentationImages")
  dokkaSourceSets {
    named("main") {
      moduleName.set("Quiver Library")

      // Includes custom documentation
      includes.from("module.md")

      // Points source links to GitHub
      sourceLink {
        localDirectory.set(file("src/main/kotlin"))
        remoteUrl.set(URL("https://github.com/cashapp/quiver/tree/master/lib/src/main/kotlin"))
        remoteLineSuffix.set("#L")
      }
    }
  }
}
