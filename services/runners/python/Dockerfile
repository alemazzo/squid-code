# Use an official minimal Python runtime
FROM python:3.14-slim

# Create a non-root user
RUN adduser --disabled-password --gecos '' coderunner

# Set working directory and change to the non-root user
WORKDIR /home/coderunner/app
USER coderunner

# Copy the runner script (we'll write a simple one)
COPY run.py .

# Set the entrypoint: The runner script will receive test input as an argument (or via STDIN)
ENTRYPOINT ["python", "run.py"]