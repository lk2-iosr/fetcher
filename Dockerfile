FROM openjdk:8
RUN mkdir -p /usr/src/app/etc
COPY etc/settings.yml /usr/src/app/etc
COPY target/fetcher-1.0-SNAPSHOT.jar /usr/src/app
WORKDIR /usr/src/app
EXPOSE 5000
CMD ["java", "-Ddw.server.connector.type=http", "-Ddw.server.connector.port=5000", "-jar", "fetcher-1.0-SNAPSHOT.jar", "server", "etc/settings.yml"]