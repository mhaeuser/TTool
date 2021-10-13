FROM debian:latest


MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>
RUN apt-get clean
RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install make git-core python3
RUN update-alternatives --install /usr/bin/python \
  python /usr/bin/python3 2
  
RUN apt-get install build-essential -y --no-install-recommends

RUN apt-get update; apt-get install -y wget

RUN wget -c --header "Cookie: oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz" -O jdk-8-linux-x64.tar.gz
RUN tar xzf jdk-8-linux-x64.tar.gz; mkdir -p /opt/java; mv jdk1.8.0_131 /opt/java; ln -s /opt/java/jdk1.8.0_131 /opt/java/latest; ln -s /opt/java/latest /opt/java/default

# JDK stripping
RUN rm -f /opt/java/jdk1.8.0_131/src.zip /opt/java/jdk1.8.0_131/javafx-src.zip
RUN rm -rf /opt/java/jdk1.8.0_131/lib/missioncontrol/ /opt/java/jdk1.8.0_131/lib/visualvm/ /opt/java/jdk1.8.0_131/db/

RUN update-alternatives --install /usr/bin/java java /opt/java/latest/bin/java 1
RUN update-alternatives --install /usr/bin/javac javac /opt/java/latest/bin/javac 1

ENV JAVA_HOME /opt/java/latest
ENV PATH $PATH:$JAVA_HOME/bin

CMD ["gradle"]

ENV GRADLE_HOME /opt/gradle

RUN set -o errexit -o nounset \
    && echo "Adding gradle user and group" \
    && groupadd --system --gid 1000 gradle \
    && useradd --system --gid gradle --uid 1000 --shell /bin/bash --create-home gradle \
    && mkdir /home/gradle/.gradle \
    && chown --recursive gradle:gradle /home/gradle \
    \
    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
    && ln -s /home/gradle/.gradle /root/.gradle

VOLUME /home/gradle/.gradle

WORKDIR /home/gradle

RUN apt-get update \
    && apt-get install --yes --no-install-recommends \
        fontconfig \
        unzip \
        wget \
        \
        bzr \
        git \
        git-lfs \
&& echo "Testing VCSes" \
    && which bzr \
    && which git \
    && which git-lfs

RUN apt-get update \
    && apt-get install --yes --no-install-recommends \
        openssh-client

ENV GRADLE_VERSION 6.9
ARG GRADLE_DOWNLOAD_SHA256=f581709a9c35e9cb92e16f585d2c4bc99b2b1a5f85d2badbd3dc6bff59e1e6dd
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Checking download hash" \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    \
    && echo "Testing Gradle installation" \
    && /opt/gradle/bin/gradle --version

ENV PATH="/opt/gradle/bin:${PATH}"

RUN rm -rf /var/lib/apt/lists/*



