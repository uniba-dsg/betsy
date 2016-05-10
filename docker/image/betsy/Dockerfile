# This is a Dockerfile to create an image with betsy installed.

# Version 1.0

FROM ubuntu

MAINTAINER Christoph Bröker(christoph.broeker@stud.uni-bamberg.de)

# install  
RUN \
     apt-get update  && \
     apt-get -y install software-properties-common && \
     add-apt-repository -y ppa:webupd8team/java && \
     apt-get update  

# Install Java7.
RUN \
  echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  apt-get install -y oracle-java7-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk7-installer

# Define commonly used JAVA_HOME variable
ENV JAVA7_HOME /usr/lib/jvm/java-7-oracle

# Install Java 8.
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# make everything up to date
RUN apt-get update &&\
    apt-get -y upgrade &&\
    apt-get clean

# git
RUN apt-get update && \
	apt-get install -y git

# when copying the Dockerfile in the repo, this is no longer necessary
# see http://stackoverflow.com/questions/27482504/dockerfile-cannot-access-git-repo-with-my-private-key
RUN mkdir ~/.ssh
RUN ssh-keyscan -t rsa github.com > ~/.ssh/known_hosts
RUN git clone https://github.com/ChristophBroeker/betsy.git && cd betsy 

WORKDIR betsy

# build betsy
RUN ./gradlew installDist
