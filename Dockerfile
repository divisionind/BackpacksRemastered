FROM ubuntu:22.04
MAINTAINER drew6017

# install jdk11/8 and set 11 as default
RUN export DEBIAN_NONINTERACTIVE=true && \
    apt update && \
    apt install -yqq --no-install-recommends openjdk-11-jdk openjdk-8-jdk git nano dos2unix && \
    rm -rf /var/lib/apt/lists/* && \
    echo "export JAVA_HOME=\"/usr/lib/jvm/java-11-openjdk-amd64\"" >> ~/.bashrc

#ADD . /project
WORKDIR /project
#RUN git ls-files -z | xargs -0 dos2unix && \
#    chmod +x gradlew && \
#    ./gradlew pack

ENTRYPOINT bash
