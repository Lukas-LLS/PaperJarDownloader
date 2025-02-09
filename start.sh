#!/usr/bin/env bash

export PATH=$PATH:/opt/java/openjdk/bin

TZ=$(curl -s "http://worldtimeapi.org/api/ip" | jq -r ".timezone")

if [ -z "$TZ" ]; then
  IP=$(curl -s "http://ifconfig.me/ip")
  TZ=$(curl -s "https://timeapi.io/api/timezone/ip?ipAddress=$IP" | jq -r ".timeZone")
fi

if [ -z "$TZ" ]; then
  echo "Failed to get timezone, using default timezone"
  TZ="UTC"
fi

export TZ
echo "Timezone is $TZ"

java -jar ./PaperJarDownloader.jar
cd ./server || exit
echo eula=true > eula.txt

java -jar -XX:+UseZGC -XX:+ZGenerational ./server.jar nogui
