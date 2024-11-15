FROM public.ecr.aws/docker/library/eclipse-temurin:21.0.3_9-jre-alpine

RUN apk add --no-cache dumb-init && \
    rm -rf /var/cache/apk/*

ENV JAVA_USER javauser
ENV WORKDIR_HOME /app

RUN mkdir ${WORKDIR_HOME} &&\
    addgroup --system ${JAVA_USER} && adduser -S -s /bin/false -G ${JAVA_USER} ${JAVA_USER} &&\
    chown ${JAVA_USER}:${JAVA_USER} ${WORKDIR_HOME}

WORKDIR  ${WORKDIR_HOME}

COPY --chown=${JAVA_USER}:${JAVA_USER} target/grpc-server*.jar ./application.jar

USER ${JAVA_USER}

EXPOSE 8081
EXPOSE 58081

CMD ["dumb-init", "java", "-jar", "application.jar"]
