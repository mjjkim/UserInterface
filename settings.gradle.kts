pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // Google repository 설정
        mavenCentral()  // MavenCentral repository 설정
    }
}

rootProject.name = "UserInterface"
include(":app")
