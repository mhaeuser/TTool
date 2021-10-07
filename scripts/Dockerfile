FROM debian:latest
MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>
RUN apt-get clean
RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install make git-core python3

ENV JAVA_VERSION 8u77
ENV BUILD_VERSION b03

WORKDIR /tmp

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



RUN update-alternatives --install /usr/bin/python \
  python /usr/bin/python3 2
  
RUN rm -f jdk-8-linux-x64.tar.gz; apt-get remove -y wget; apt-get autoremove -y; apt-get clean -y; apt-get autoclean -y
