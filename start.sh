su -c "java -jar ./PaperJarDownloader.jar" server
su -c "cd ./server || exit" server
su -c "echo eula=true > eula.txt" server
su -c "java -jar ./server.jar nogui" server