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

# Set java enviroment
ENV LANG=C.UTF-8 \
	JAVA_MAJOR_VERSION=11 \
	JAVA_MINOR_VERSION=0 \
	JAVA_UPDATE=1 \
	JAVA_BUILD=13 \
	JAVA_PATH=90cf5d8f270a4347a95050320eef3fb7 \
	JAVA_TYPE=jdk

ENV JAVA_FULL_VERSION="${JAVA_MAJOR_VERSION}.${JAVA_MINOR_VERSION}.${JAVA_UPDATE}"

ENV JAVA_HOME="/opt/java/${JAVA_TYPE}-${JAVA_FULL_VERSION}" \
	JAVA_TAR="${JAVA_TYPE}-${JAVA_FULL_VERSION}_linux-x64_bin.tar.gz"

# Downloading oracle jdk -> extract it -> cleanup -> add app user & group
# You can use USER called 'app' for your application
RUN cd /tmp \
	&& apt-get update \
	&& apt-get install -y wget \
	&& mkdir -p $JAVA_HOME \
	&& wget --header "Cookie: oraclelicense=accept-securebackup-cookie;" \
	--progress=dot:mega --tries=10 "http://download.oracle.com/otn-pub/java/jdk/${JAVA_FULL_VERSION}+${JAVA_BUILD}/${JAVA_PATH}/${JAVA_TAR}" \
	&& tar -xzf $JAVA_TAR -C /opt/java \
	&& ln -s $JAVA_HOME $JAVA_HOME/bin/* /usr/bin/ \
	&& rm -rf $JAVA_HOME/lib/src.zip \
	$JAVA_HOME/lib/missioncontrol \
	$JAVA_HOME/lib/*javafx* \
	$JAVA_HOME/lib/*jfx* \
	$JAVA_HOME/lib/*awt* \
	$JAVA_HOME/lib/desktop \
	$JAVA_HOME/lib/javaws.jar \
	$JAVA_HOME/lib/plugin.jar \
	$JAVA_HOME/lib/plugin-legacy.jar \
	$JAVA_HOME/lib/javaws.jar \
	$JAVA_HOME/lib/desktop \
	$JAVA_HOME/lib/deploy \
	$JAVA_HOME/lib/deploy* \
	$JAVA_HOME/lib/fonts \
	$JAVA_HOME/lib/oblique-fonts \
	$JAVA_HOME/jmods \
    && apt-get remove -y wget \
    && apt-get clean -y \
    && apt-get autoremove -y \
    && apt-get autoclean -y \
    && rm -rf /tmp/* \
    && rm -rf /var/cache/apt/archives/* \
	&& useradd -ms /bin/bash app

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
        \
        bzr \
        git \
        git-lfs \
        mercurial \
        openssh-client \
        subversion \
    && rm --recursive --force /var/lib/apt/lists/* \
    \
    && echo "Testing VCSes" \
    && which bzr \
    && which git \
    && which git-lfs \
    && which hg \
    && which svn

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


RUN rm -rf /var/lib/apt/lists/*



