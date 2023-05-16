FROM gradle:7.4-jdk11-focal
MAINTAINER Ludovic Apvrille <ludovic.apvrille@telecom-paris.fr>

# Basic tools
RUN apt-get update && apt-get install -yqq make git-core python3 build-essential wget zip software-properties-common bash opam
RUN rm -rf /var/lib/apt/lists/*

# Install Proverif
RUN apt-get install -yqq libexpat1-dev libgtk2.0-dev opam
RUN opam init --best-effort -y && eval $(opam env) && opam install proverif --best-effort -y


CMD /bin/bash




