#FROM eclipse-temurin:21-jdk-alpine
#VOLUME /tmp
#COPY *.jar LoanApplicationsService.jar
#ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
## Replace with a non-root user to avoid running the container with excessive privileges
#USER appuser
#ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar LoanApplicationsService.jar" ]


FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY ../applications/app-service/build/libs/LoanApplicationsService.jar /app/LoanApplicationsService.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar LoanApplicationsService.jar" ]