# PaperJarDownloader
## Introduction
PaperJarDownloader is a tool to automatically download the latest version of the PaperMC server jar file.
If a specific version is specified, it will download that version instead.

## Building
To build PaperJarDownloader, you need to have the following installed:
- [Java 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)

Once you have those installed, you can build PaperJarDownloader by running the following command in the project directory
```bash
mvn package
```

## Usage
Copy the output jar file from the `target` directory to the directory you want to run PaperJarDownloader from.
Then, run the following commands to download the latest version of the PaperMC server jar file and start the server.
```bash
java -jar PaperJarDownloader.jar

java -jar server.jar nogui
```

If you want to download a specific version of the PaperMC server jar file, run the following command.
```bash
java -jar PaperJarDownloader.jar <version>
```