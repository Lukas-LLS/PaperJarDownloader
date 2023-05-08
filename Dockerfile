FROM eclipse-temurin:17-jre

MAINTAINER LukasLLS

EXPOSE 25565

RUN useradd -M server

RUN mkdir /app

COPY start.sh /app/start.sh

COPY target/PaperJarDownloader-2.0.0-jar-with-dependencies.jar /app/PaperJarDownloader.jar

RUN chmod +x /app/start.sh

RUN chown -R server:server /app

WORKDIR /app

CMD ["su", "-c", "./start.sh", "server"]