FROM instructure/java:9

USER root
WORKDIR /usr/src/light_bikes

USER docker

COPY --chown=docker:docker . /usr/src/light_bikes
COPY --chown=docker:docker ../light_bikes_ui/. /usr/src/light_bikes/ui

EXPOSE 8080
EXPOSE 8443

CMD ["./gradlew", "bootRun"]
