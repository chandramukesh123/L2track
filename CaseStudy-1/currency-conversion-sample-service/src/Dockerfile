# Use the latest Tomcat image from Docker Hub
FROM mukesh243/tomcat:latest

# Arguments passed from buildspec.yml
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

# Install awscli to interact with AWS services
RUN apt-get update && \
    apt-get install -y awscli

# Set environment variables for AWS credentials (replace with your AWS credentials or use IAM roles)
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
ENV AWS_DEFAULT_REGION=us-east-1

# Create directory to store WAR file
RUN mkdir -p /usr/local/tomcat/webapps/myapp

# Copy WAR file from S3 bucket to webapps folder
RUN aws s3 cp s3://sample-bucket76tjrf/sample-maven/target/wwp-1.0.0.war /usr/local/tomcat/webapps/myapp/wwp-1.0.0.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
