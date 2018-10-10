FROM instructure/java:8

USER root
WORKDIR /usr/src/light_bikes

USER docker

COPY --chown=docker:docker . /usr/src/light_bikes

EXPOSE 8080
EXPOSE 8443

CMD ["./gradlew", "bootRun"]
