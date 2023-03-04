FROM eclipse-temurin:17-jre

MAINTAINER LukasLLS

EXPOSE 25565

RUN mkdir /app

COPY start.sh /app/start.sh

COPY target/*jar-with-dependencies.jar /app/PaperJarDownloader.jar

RUN chmod +x /app/start.sh

WORKDIR /app

CMD ["bash", "start.sh"]