FROM --platform=linux/arm64 public.ecr.aws/amazonlinux/amazonlinux:2

RUN yum -y update && \
    yum install -y zip unzip tar gzip bzip2-devel ed gcc gcc-c++ gcc-gfortran   \
      less libcurl-devel openssl openssl-devel readline-devel xz-devel          \
      zlib-devel glibc-static libcxx libcxx-devel llvm-toolset-7 zlib-static && \
    rm -rf /var/cache/yum

# Verify latest LTS version at: https://github.com/graalvm/graalvm-ce-builds/releases
ENV JAVA_VERSION java17
ENV GRAAL_VERSION 22.3.3
ENV GRAAL_FOLDERNAME graalvm-ce-${JAVA_VERSION}-${GRAAL_VERSION}
ENV GRAAL_FILENAME graalvm-ce-${JAVA_VERSION}-linux-aarch64-${GRAAL_VERSION}.tar.gz
RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} | tar -xvz
RUN mv "$GRAAL_FOLDERNAME" /usr/lib/graalvm
RUN rm -rf "$GRAAL_FOLDERNAME"

# Verify latest version at: https://dlcdn.apache.org/maven/maven-3/
ENV MVN_VERSION 3.9.4
ENV MVN_FOLDERNAME apache-maven-${MVN_VERSION}
ENV MVN_FILENAME apache-maven-${MVN_VERSION}-bin.tar.gz
RUN curl -4 -L https://dlcdn.apache.org/maven/maven-3/${MVN_VERSION}/binaries/${MVN_FILENAME} | tar -xvz
RUN mv "$MVN_FOLDERNAME" /usr/lib/maven
RUN rm -rf "$MVN_FOLDERNAME"

# Graal native image builder
RUN /usr/lib/graalvm/bin/gu install native-image
RUN ln -s /usr/lib/graalvm/bin/java         /usr/bin/java
RUN ln -s /usr/lib/graalvm/bin/jar          /usr/bin/jar
RUN ln -s /usr/lib/graalvm/bin/native-image /usr/bin/native-image
RUN ln -s /usr/lib/maven/bin/mvn            /usr/bin/mvn
ENV JAVA_HOME /usr/lib/graalvm

# AWS Lambda Builders
RUN amazon-linux-extras enable python3.8
RUN yum clean metadata && yum -y install python3.8
RUN curl -L get-pip.io | python3.8
RUN pip3 install aws-lambda-builders

VOLUME /workspace
WORKDIR /workspace

ENTRYPOINT ["sh"]
