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

## To do

- Add prometheus library to enable metrics
- Add micrometer library to enable tracing
- Add Archunit test library and tests to ensure clean structure
- Add static analysis & linting tooling
  - Add PMD
  - Add SpotBugs
  - Add OWASP Dependency-Check
  - Add checkstyle
- Add persistence
    - (Optional) Consider test containers
- Extend application API
  - Add OpenApi contract
  - (Optional) Add rate limiting
  - (Optional) Add HATEOAS
- (Optional) Add JavaDoc rendering to documentation
- (Optional) Add security library
  - Consider LDAP
  - Consider OAuth2
- (Optional) Add feature flags library
