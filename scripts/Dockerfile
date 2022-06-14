FROM gradle:7.4-jdk11-focal
MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>

RUN apt-get update && apt-get install -yqq make git-core python3 build-essential wget zip software-properties-common bash
RUN rm -rf /var/lib/apt/lists/*
CMD /bin/bash


