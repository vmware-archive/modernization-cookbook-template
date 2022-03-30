FROM fedora:latest

RUN dnf install -y nodejs python findutils git \
        java java-devel \
  	&& dnf clean all \
  	&& rm -rf /var/cache/yum

ENV JAVA_HOME /usr/lib/jvm/java-openjdk
