#!/usr/bin/env bash

export PATH=$PATH:/opt/java/openjdk/bin
TZ=$(curl -s 'http://worldtimeapi.org/api/ip' | jq -r '.timezone')
export TZ
java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt
java -jar ./server.jar nogui