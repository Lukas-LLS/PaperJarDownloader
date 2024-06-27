# Use the Eclipse Temurin JRE 21 as the base image
FROM eclipse-temurin:21-jre

# Set the author of the image
LABEL org.opencontainers.image.authors="LukasLLS"

# Expose port 25565 for external connections
EXPOSE 25565

# Update the package lists
RUN apt update

# Install the jq package (used for parsing JSON)
RUN apt install jq -y

# Upgrade all packages
RUN apt full-upgrade -y

# Remove unnecessary packages
RUN apt autoremove -y

# Create a new user named 'server' without a home directory
RUN useradd -M server

# Create a directory for the application
RUN mkdir /app

# Copy the start script to the /app directory
COPY start.sh /app/start.sh

# Copy the application JAR file to the /app directory
COPY target/PaperJarDownloader-2.2.0-jar-with-dependencies.jar /app/PaperJarDownloader.jar

# Make the start script executable
RUN chmod +x /app/start.sh

# Change the ownership of the /app directory to the 'server' user
RUN chown -R server:server /app

# Set the working directory to /app
WORKDIR /app

# Switch to the 'server' user
USER server

# Set the start script as the entrypoint
CMD ["./start.sh"]
