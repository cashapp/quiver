plugins {
  `java-library`
}

sourceSets {
  val test by getting {
    java.srcDir("src/test/kotlin/")
  }
}

dependencies {
  implementation(project(":lib"))
  implementation(libs.arrowCore)
  implementation(libs.kotestProperty)
  implementation(libs.kotestPropertyArrow)

  testImplementation(libs.junitApi)
  testImplementation(libs.kotestAssertions)
  testImplementation(libs.kotestAssertionsArrow)
  testImplementation(libs.kotestFrameworkApi)
  testImplementation(libs.kotestJunitRunnerJvm)

  testRuntimeOnly(libs.junitEngine)
}
