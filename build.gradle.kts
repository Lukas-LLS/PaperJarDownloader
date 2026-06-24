val ktorVersion = "3.5.0"
val apolloVersion = "5.0.0"
val logbackVersion = "1.5.35"
val slf4jVersion = "2.0.18"

plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.2"
    id("com.apollographql.apollo") version "5.0.0"
}

group = "lls.pjd"
version = "3.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("com.apollographql.apollo:apollo-runtime:$apolloVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
}

kotlin {
    jvmToolchain(25)
}

apollo {
    service("PaperJarDownloader") {
        packageName.set("lls.pjd.generated")
        schemaFile.set(file("src/main/graphql/schema.graphqls"))
        introspection {
            endpointUrl.set("https://fill.papermc.io/graphql")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "lls.pjd.Main"
    }
}