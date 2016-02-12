jclouds Labs - Azure Compute ARM Provider

Build status for azurecomputearm module:
[![Build Status](https://jclouds.ci.cloudbees.com/buildStatus/icon?job=jclouds-labs/org.apache.jclouds.labs$azurecompute-arm)](https://jclouds.ci.cloudbees.com/job/jclouds-labs/org.apache.jclouds.labs$azurecompute-arm/)



## Setting Up Test Credentials

### Create a Service Principal

Install and configure Azure CLI following these [steps](http://azure.microsoft.com/en-us/documentation/articles/xplat-cli/).

Using the Azure CLI, run the following commands to create a service principal

```bash
# Set mode to ARM
azure config mode arm

# Enter your Microsoft account credentials when prompted
azure login

# Set current subscription to create a service principal
azure account set <Subscription-id>

# Create an AAD application with your information.
azure ad app create --name <name> --password <password> --home-page <home-page> --identifier-uris <identifier-uris>

# For example: azure ad app create --name "jcloudsarm"  --password abcd --home-page "https://jcloudsarm" --identifier-uris "https://jcloudsarm"

# Output will include a value for `Application Id`, which will be used for the live tests

# Create a Service Principal
azure ad sp create <Application-id>

# Output will include a value for `Object Id`

```

Run the following commands to assign roles to the service principal

```bash
# Assign roles for this service principal
azure role assignment create --objectId <Object-id> -o Contributor -c /subscriptions/<Subscription-id>/
```

Verify service principal

```bash
azure login -u <Application-id> -p <password> --service-principal --tenant <Tenant-id>
```

## Run Live Tests

Use the following to run the live tests

```bash

mvn clean verify -Plive \
    -Dtest.azurecompute-arm.identity=<Application-id> \
    -Dtest.azurecompute-arm.credential=<password> \
    -Dtest.azurecompute-arm.endpoint=https://management.azure.com/subscriptions/<Subscription-id> \
    -Dtest.oauth.endpoint=https://login.microsoftonline.com/<Tenant-id>/oauth2/token
```
## Annotation processing
In order for value object and service loader auto-generation, you will need to enable annotation processing in your IDE.
If you notice any inconsistencies in your IDE after you enable annotation processing, try rerunning "Make Project".

## Setting up test credentials

Azure requests are signed by via SSL certificate. You need to upload one into your account in order to run tests.

```bash
# create the certificate request
mkdir -m 700 $HOME/.jclouds
openssl req -x509 -nodes -days 365 -newkey rsa:1024 -keyout $HOME/.jclouds/azure.pem -out $HOME/.jclouds/azure.pem
# create the p12 file, and note your export password. This will be your test credentials.
openssl pkcs12 -export -out $HOME/.jclouds/azure.p12 -in $HOME/.jclouds/azure.pem -name "jclouds :: $USER"
# create a cer file which you upload to the management console to authorize this certificate.
# https://manage.windowsazure.com/@ignasibarreragmail.onmicrosoft.com#Workspaces/AdminTasks/ListManagementCertificates
# note you need to press command+shift+. to display hidden directories in a open dialog in osx
openssl x509 -inform pem -in $HOME/.jclouds/azure.pem -outform der -out $HOME/.jclouds/azure.cer
```

Once you do this, you will set the following to run the live tests.
```bash
mvn -Plive -Dtest.azurecomputearm.endpoint=https://management.core.windows.net/12345678-abcd-dcba-abdc-ba0987654321 \
-Dtest.azurecomputearm.credential=P12_EXPORT_PASSWORD \
-Dtest.azurecomputearm.identity=$HOME/.jclouds/azure.p12
```
