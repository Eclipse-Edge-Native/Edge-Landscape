tasks.create("build") {
    group = "build"
    dependsOn(gradle.includedBuild("landscape").task(":build"))
}

tasks.create("clean") {
    group = "build"
    dependsOn(gradle.includedBuild("landscape").task(":clean"))
}