export PATH=$PATH:/opt/java/openjdk/bin
su ln -sf /usr/share/zoneinfo/$(curl -s 'http://worldtimeapi.org/api/ip' | jq -r '.timezone') /etc/localtime
java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui