FROM eclipse-temurin:17-jre

MAINTAINER LukasLLS

EXPOSE 25565

RUN apt update

RUN apt install jq -y

RUN apt full-upgrade -y

RUN apt autoremove -y

RUN useradd -M server

RUN mkdir /app

COPY start.sh /app/start.sh

COPY target/PaperJarDownloader-2.1.0-jar-with-dependencies.jar /app/PaperJarDownloader.jar

RUN chmod +x /app/start.sh

RUN chown -R server:server /app

WORKDIR /app

CMD ["su", "-c", "./start.sh", "server"]
