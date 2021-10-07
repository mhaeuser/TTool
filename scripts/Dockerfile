FROM gradle:4.7.0-jdk8-alpine AS build
FROM debian:latest


MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>
RUN apt-get clean
RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install make git-core python3
RUN update-alternatives --install /usr/bin/python \
  python /usr/bin/python3 2
  
RUN apt-get install build-essential -y --no-install-recommends




