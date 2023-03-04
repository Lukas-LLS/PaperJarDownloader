FROM eclipse-temurin:17-jre

MAINTAINER LukasLLS

EXPOSE 25565

RUN mkdir /app

COPY start.sh /app/start.sh

COPY target/PaperJarDownloader-1.0-SNAPSHOT-jar-with-dependencies /app/PaperJarDownloader.jar

RUN chmod +x /app/start.sh

WORKDIR /app

CMD ["bash", "start.sh"]