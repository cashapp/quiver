plugins {
  `java-library`
}

dependencies {
  implementation(kotlin("reflect"))
  implementation(libs.arrowCore)

  testImplementation(project(":testing-lib"))
  testImplementation(libs.junitApi)
  testImplementation(libs.kotestAssertions)
  testImplementation(libs.kotestAssertionsArrow)
  testImplementation(libs.kotestFrameworkApi)
  testImplementation(libs.kotestJunitRunnerJvm)
  testImplementation(libs.kotestProperty)
  testImplementation(libs.kotestPropertyArrow)

  testRuntimeOnly(libs.junitEngine)
}
