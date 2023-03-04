java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui