# Use an official OpenJDK slim image
FROM openjdk:17-slim

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