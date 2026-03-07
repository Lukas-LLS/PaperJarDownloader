# PaperJarDownloader

[![Upload to Docker Hub](https://github.com/Lukas-LLS/PaperJarDownloader/actions/workflows/docker.yml/badge.svg)](https://github.com/Lukas-LLS/PaperJarDownloader/actions/workflows/docker.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/lukas-lls/paperjardownloader/badge)](https://www.codefactor.io/repository/github/lukas-lls/paperjardownloader)

## Introduction

PaperJarDownloader is a tool to automatically download the latest version of the PaperMC server jar file.
If a specific version is specified, it will download that version instead.

## Building (Optional)

To build PaperJarDownloader, you need to have the following installed:

- [Java 25](https://www.oracle.com/java/technologies/downloads/#java25)
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

If you want to download a specific version of the PaperMC server jar file, specify an environment variable called
`MC_VERSION`.

For example, to download version 1.18.2, you can run the following command.

```bash
export MC_VERSION=1.18.2
java -jar PaperJarDownloader.jar
```

### Variant - Folia

Likewise, you can set the environment variable `FOLIA` (Any value works, as long as the variable is set) to download the
latest version of Folia instead of Paper.

```bash
export FOLIA=true
java -jar PaperJarDownloader.jar
```

### Docker

If you want to use the Docker image, you can skip the building step.
The docker image is available on [Docker Hub](https://hub.docker.com/r/lukaslls/paper-jar-downloader).
You can run the image as a docker container by running the following command.

```bash
docker run -it --rm -p "25565:25565" -v "$(pwd)/server:/app/server" lukaslls/paper-jar-downloader:latest
```

Note that this will create a directory called `server` in the current directory.
This directory will contain the server files.

If you want to specify a version, you can run the following command.

```bash
docker run -it --rm -p "25565:25565" -v "$(pwd)/server:/app/server" -e "MC_VERSION=1.18.2" lukaslls/paper-jar-downloader:latest
```

## Noteworthy

The docker image uses Java 25 by default and will automatically download the latest version.
