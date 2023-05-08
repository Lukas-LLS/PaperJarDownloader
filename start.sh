/opt/java/openjdk/bin/java -jar ./PaperJarDownloader.jar
cd /app/server || exit
echo eula=true > eula.txt
pwd
/opt/java/openjdk/bin/java -jar ./server.jar nogui