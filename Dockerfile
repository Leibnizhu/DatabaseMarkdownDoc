FROM java:8u111-jre-alpine
COPY target/docbuilder-1.0-SNAPSHOT-fat.jar /usr/local
COPY target/classes/config.json /usr/local
WORKDIR /usr/local
CMD java -jar docbuilder-1.0-SNAPSHOT-fat.jar /usr/local/config.json