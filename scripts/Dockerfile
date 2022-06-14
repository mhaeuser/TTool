FROM debian:latest


MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>
RUN apt-get clean
RUN apt-get update
RUN apt-get -y install dialog apt-utils
RUN apt-get -y install make git-core python3
  
RUN apt-get install build-essential -y --no-install-recommends

RUN apt-get update; apt-get install -y wget


# Downloading oracle jdk -> extract it -> cleanup -> add app user & group
# You can use USER called 'app' for your application
RUN cd /tmp \
	&& apt-get update \
	&& apt-get install -y wget \
	&& apt-get install -y openjdk-11-jre

# Add java to path
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
    && ln --symbolic /home/gradle/.gradle /root/.gradle

VOLUME /home/gradle/.gradle

WORKDIR /home/gradle

RUN set -o errexit -o nounset \
    && apt-get update \
    && apt-get install --yes --no-install-recommends \
        unzip \
        wget \
        bzr \
        git \
        git-lfs \
    \
    && echo "Testing VCSes" \
    && which bzr \
    && which git \
    && which git-lfs 
 
ENV GRADLE_VERSION 7.4.2
ARG GRADLE_DOWNLOAD_SHA256=29e49b10984e585d8118b7d0bc452f944e386458df27371b49b4ac1dec4b7fda
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Checking download hash" \
    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check - \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    \
    && echo "Testing Gradle installation" \
    && gradle --version


RUN rm -rf /var/lib/apt/lists/*



