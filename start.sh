/opt/java/openjdk/bin/java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
/opt/java/openjdk/bin/java -jar ./server.jar nogui