import { AuthConfig } from 'angular-oauth2-oidc';
import { DefaultDataServiceConfig } from '@ngrx/data';

export const environment = {
  production: false,
  timeZone: 'America/Guayaquil'
};

export const defaultDataServiceConfig: DefaultDataServiceConfig = {
  root: 'ALB_API_ENDPOINT/api/v1',
  timeout: 3000,
  delete404OK: false,
  trailingSlashEndpoints: false
};

export const authConfig: AuthConfig = {
  issuer: 'https://cognito-idp.IDP_AWS_REGION.amazonaws.com/COGNITO_USER_POOL_ID',
  oidc: true,
  strictDiscoveryDocumentValidation: false,
  clientId: 'COGNITO_APP_CLIENT_ID_WEB',
  redirectUri: 'https://AWS_WORKLOADS_ENV.AMPLIFY_APP_ID.amplifyapp.com/', // MUST use the root domain for the Amplify Hosting.
  responseType: 'code',
  scope: 'phone email openid profile aws.cognito.signin.user.admin',
  showDebugInformation: environment.production
};
