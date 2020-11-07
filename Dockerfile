FROM openjdk

SHELL ["/usr/bin/bash", "-c"]

RUN microdnf -y install findutils which zip

ENV SDKMAN_DIR $HOME/.sdkman

RUN curl -s "https://get.sdkman.io" | bash

RUN set -x \
    && echo "sdkman_auto_answer=true" > $SDKMAN_DIR/etc/config \
    && echo "sdkman_auto_selfupdate=false" >> $SDKMAN_DIR/etc/config \
    && echo "sdkman_insecure_ssl=false" >> $SDKMAN_DIR/etc/config

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN source $SDKMAN_DIR/bin/sdkman-init.sh \
    && sdk install java 15.0.1-open \
    && sdk install sbt 1.4.2 \
    && sdk env \
    && sbt stagePackage


