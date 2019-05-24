HOSTING
===============
How to host the application.

#Google Cloud Run
1. Create a new app in [Google page](https://console.cloud.google.com)
2. Create a Service Account Key in [Credentials page](https://console.cloud.google.com/apis/credentials) with role "Project - Owner" and save it as google-key.json
3. Enable Cloud Run
4. In repository create the Repository variable
    GOOGLE_PROJECT_ID the ID generated in step 1
5. Create the Dockerfile
6. Create the pipelines file in order to deploy docker image to Container Registry and deploy version

#Heroku
1. Generate an API Key in Account Settings > Generate API Key
2. Create a new app in [Heroku page](https://dashboard.heroku.com/apps)
3. In Heroku app settings add the Config Vars SPRING_PROFILES_ACTIVE with value h2
4. In repository create the Repository variables
    HEROKU_API_KEY the Key generated in step 1<br>
    HEROKU_APP_NAME the Name of project generated in step 2
5. Create the system.properties file with Java version (example: java.runtime.version=12)
6. Create the pipelines file in order to push to heroku repository

#AWS (JAR)
##Create a deploy Group
1. AWS > IAM > Groups > Create New Group
2. Type "deploy_group" as Group Name
3. Attach "AmazonS3FullAccess" and "AWSElasticBeanstalkFullAccess" Policies

##Create an IAM user
1. AWS > IAM > Users > Add User
2. Type "ragde_java_api_deploy" as User Name
3. Check "Programmatic access" Access type
4. Check "deploy_group" to enroll this user with deploy Group
5. Save Access key ID and Secret access key (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY variable)

##Create a S3 Bucket
1. AWS > S3 > Create bucket
2. Type "ragdejavaapi" as Bucket Name (AWS_S3Bucket variable)

##Create an Elastic Beanstalk Application
1. AWS > Elastic Beanstalk > Create a New Application
2. Type "RagdeJavaApi" as Application Name (AWS_ELASTIC_APP_NAME variable)
3. In "RagdeJavaApi" create a new Web server environment called "Ragde-java-api" (AWS_ELASTIC_ENVIRONMENT variable)
4. Type "ragde" as Domain name
5. Choose Java as Platform
6. Choose "Sample application"
7. Once Created, in Configuration:
8. Set SPRING_PROFILES_ACTIVE = aws on Application Configuration -> Software Configuration -> Environment Properties
9. Create a new RDS database
10. Choose mysql as Engine with max version
11. Choose db.t2.micro as Instance class
12. Set username and password
13. Choose Delete when environment is deleted

##In Repository create the 6 Repository variables
1. AWS_ACCESS_KEY_ID
2. AWS_SECRET_ACCESS_KEY
3. AWS_S3Bucket
4. AWS_ELASTIC_APP_NAME
5. AWS_ELASTIC_ENVIRONMENT
6. AWS_REGION (with value "us-east-2")

#AWS Elastic Beanstalk Manually (WAR)
##Create a New Application
1. Create a New Application in Elastic Beanstalk
2. Choose Tomcat as Platform
3. Choose Upload your code and upload a war
4. Click on Configure more options
5. Modify Environment settings (Name and Domain)
6. Modify Database settings
7. Choose mysql as Engine with max version
8. Choose db.t2.micro as Instance class
9. Set username and password
10. Choose Delete when environment is deleted
11. Click on Create app
12. Set spring.profiles.active = aws on Application Configuration -> Software Configuration -> Environment Properties

##Allow DB access from my IP
1. In EC2 > Security Groups select the Group that "Enable database access to Beanstalk application"
2. In Inbound tap add a new rule Type: MYSLQ Source: My IP

##Run locally to create data base
1. In AWS database create "ragde" database
2. In application-sql-local.properties set:<br>
    spring.jpa.hibernate.ddl-auto = create<br>
    spring.datasource.url to point to remote AWS database
3. Run locally with ./gradlew bootRun -Dspring.profiles.active=sql-aws