package org.jclouds.azurecompute.arm.compute.strategy;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;

import static org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata.AZURE_LOGIN_PASSWORD;
import static org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata.AZURE_LOGIN_USERNAME;

public class AzurePopulateDefaultLoginCredentialsForImageStrategy implements PopulateDefaultLoginCredentialsForImageStrategy {
   @Override
   public LoginCredentials apply(Object o) {
      Credentials creds = new Credentials(AZURE_LOGIN_USERNAME, AZURE_LOGIN_PASSWORD);
      LoginCredentials credentials = LoginCredentials.fromCredentials(creds);
      return credentials;
   }
}
