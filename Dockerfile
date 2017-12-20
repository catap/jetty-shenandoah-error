FROM fedora:27
RUN set -x \
	&& yum install -y java-1.8.0-openjdk unzip \
	&& yum clean all
COPY target/jetty-shenandoah-error-1.0.0-dist.zip  /server/jetty-shenandoah-error-1.0.0-dist.zip
RUN unzip /server/jetty-shenandoah-error-1.0.0-dist.zip -d /opt/
RUN rm -rf /server
EXPOSE 80
WORKDIR /opt/jetty-shenandoah-error-1.0.0/bin/
ENTRYPOINT ["java", "-server", "-Xmx1G", "-XX:+UseShenandoahGC", "-XX:+UnlockDiagnosticVMOptions", "-XX:ShenandoahGCHeuristics=aggressive", "-jar", "jetty-shenandoah-error-1.0.0.jar"]
