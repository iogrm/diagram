FROM hub.hamdocker.ir/sbtscala/scala-sbt:eclipse-temurin-alpine-22_36_1.10.0_2.13.14

WORKDIR /app
COPY project/build.properties project/plugins.sbt /app/project/
COPY build.sbt /app/

RUN sbt update

COPY src/ /app/src/

RUN sbt 'universal:stage'


CMD [ "sh" , "-c" , "target/universal/stage/bin/gapify"]