ARG REGISTRY="azsdkengsys.azurecr.io"
FROM ${REGISTRY}/java/jdk-mariner-mvn:jdk11-latest as builder

RUN yum update -y
RUN mkdir /stress-sb
WORKDIR /stress-sb

ADD ./sdk/tools /stress-sb/sdk/tools
ADD ./sdk/parents /stress-sb/sdk/parents
ADD ./sdk/core /stress-sb/sdk/core
ADD ./sdk/servicebus /stress-sb/sdk/servicebus
ADD ./eng /stress-sb/eng

RUN --mount=type=cache,target=/root/.m2 \
 mvn -f /stress-sb/eng/code-quality-reports/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/tools/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/core/azure-core/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/core/azure-core-test/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/core/azure-core-amqp/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/core/azure-core-http-netty/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/core/azure-core-metrics-opentelemetry/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/servicebus/azure-messaging-servicebus/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests && \
 mvn -f /stress-sb/sdk/servicebus/azure-messaging-servicebus-stress/pom.xml clean install -Dcheckstyle.skip -Dgpg.skip -Dmaven.javadoc.skip -Drevapi.skip -Dspotbugs.skip -Djacoco.skip -DskipTests

FROM mcr.microsoft.com/openjdk/jdk:11-mariner

RUN yum update -y

WORKDIR /app
COPY --from=builder /stress-sb/sdk/servicebus/azure-messaging-servicebus-stress/target .

ARG AGENT_URL=https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.15/applicationinsights-agent-3.4.15.jar
ADD ${AGENT_URL} ./BOOT-INF/classes/

ENTRYPOINT ["java", "-javaagent:BOOT-INF/classes/applicationinsights-agent-3.4.15.jar", \
            "-jar", "/app/azure-messaging-servicebus-stress/target/azure-messaging-servicebus-stress-1.0.0-beta.1.jar", \
            "--TEST_CLASS=MessageSender"]
