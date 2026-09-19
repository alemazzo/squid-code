# Eclipse Temurin is the maintained OpenJDK distribution: the legacy
# `openjdk` Docker Hub image has no 25 tag, so the build failed to resolve it.
FROM eclipse-temurin:25-jdk

# Create a non-root user
RUN adduser --disabled-password --gecos '' coderunner

# Set working directory
WORKDIR /home/coderunner/app

# Copy the runner script and change permissions
COPY run.sh .
RUN chmod +x run.sh

# Change to non-root user
USER coderunner

# Set the entrypoint
ENTRYPOINT ["./run.sh"]