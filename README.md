#  NBP web proxy

## Commiting guidelines

Commits follow a "[good commit message](https://cbea.ms/git-commit/)" convention.

## Dependency installation

1. (Optional) Install [SdkMan!](https://sdkman.io/install/).
2. Install Java 21.
    1. (Optional) Install using SdkMan!: `sdk install java 21.0.4-tem`.
    2. (Optional) Install using the [website](https://adoptium.net/en-GB/temurin/releases/).
3. Install Gradle 8.10
    1. (Optional) Install using SdkMan!: `sdk install gradle 8.10`.
    2. (Optional) Install [manually](https://gradle.org/next-steps/?version=8.10&format=all).
    3. (Optional) Install as wrapper with existing gradle: `gradle wrapper --gradle-version 8.10`.

## Build instruction

1. Build application
   1. Build using `gradle bootJar`
   2. (Optional) Build using docker `bash ./build.sh` on linux
   3. (Optional) Build using docker `build.ps1` on windows
