# PaperJarDownloader

[![Upload to Docker Hub](https://github.com/Lukas-LLS/PaperJarDownloader/actions/workflows/docker.yml/badge.svg)](https://github.com/Lukas-LLS/PaperJarDownloader/actions/workflows/docker.yml)

## Introduction

PaperJarDownloader is a tool to automatically download the latest version of the PaperMC server jar file.
If a specific version is specified, it will download that version instead.

## Building (Optional)

To build PaperJarDownloader, you need to have the following installed:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/) (Optional)

Once you have those installed, you can build the PaperJarDownloader by running the following command in the project
directory

```bash
mvn package
```

## Usage

### Direct

Copy the output jar file from the `target` directory
(it should be named something like `PaperJarDownloader-2.2.0-jar-with-dependencies.jar`)
to the directory you want to run PaperJarDownloader from.
Make sure that the jar file is named `PaperJarDownloader.jar`.
Then, run the following commands to download the latest version of the PaperMC server jar file and start the server.

```bash
java -jar PaperJarDownloader.jar

java -jar server/server.jar nogui
```

If you want to download a specific version of the PaperMC server jar file, run the following command.

```bash
java -jar PaperJarDownloader.jar <version>
```

### Docker

If you want to use the Docker image, you can skip the building step.
The docker image is available on [Docker Hub](https://hub.docker.com/r/lukaslls/paper-jar-downloader).
You can run the image as a docker container by running the following command.

```bash
docker run -it --rm -p "25565:25565" -v "$(pwd)/server:/app/server" lukaslls/paper-jar-downloader
```

Note that this will create a directory called `server` in the current directory.
This directory will contain the server files.

## Noteworthy

Depending on your minecraft version, you might need a different version of Java.
The required Java version for every version from 1.18.2 to 1.20.2 is Java 21.
Versions below 1.18.2 require a lower Java version.

The docker image uses Java 21 by default and will automatically download the latest version.
