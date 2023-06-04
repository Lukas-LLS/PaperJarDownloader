export PATH=$PATH:/opt/java/openjdk/bin
su -c "ln -sf /usr/share/zoneinfo/$(curl -s 'http://worldtimeapi.org/api/ip' | jq -r '.timezone') /etc/localtime" root
java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui