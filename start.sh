export PATH=$PATH:/opt/java/openjdk/bin
java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui