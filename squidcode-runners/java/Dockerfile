# Use an official OpenJDK slim image
FROM openjdk:17-slim

# Create a non-root user
RUN adduser --disabled-password --gecos '' coderunner

# Set working directory and change to non-root user
WORKDIR /home/coderunner/app
USER coderunner

# Copy the runner script (we’ll write a simple shell script that compiles and runs)
COPY run.sh .
RUN chmod +x run.sh

# Set the entrypoint
ENTRYPOINT ["./run.sh"]
