export PATH=$PATH:/opt/java/openjdk/bin
timedatectl set-timezone "$(curl -s 'http://worldtimeapi.org/api/ip' | jq -r '.timezone')"
java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui