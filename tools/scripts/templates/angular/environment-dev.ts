import { AuthConfig } from 'angular-oauth2-oidc';
import { DefaultDataServiceConfig } from '@ngrx/data';

export const environment = {
  production: false,
  timeZone: 'America/Guayaquil'
};

export const defaultDataServiceConfig: DefaultDataServiceConfig = {
  root: 'https://ALB_API_ENDPOINT/api/v1',
  timeout: 3000,
  delete404OK: false,
  trailingSlashEndpoints: false
};

export const authConfig: AuthConfig = {
  issuer: 'https://cognito-idp.IDP_AWS_REGION.amazonaws.com/COGNITO_USER_POOL_ID',
  oidc: true,
  strictDiscoveryDocumentValidation: false,
  clientId: 'COGNITO_APP_CLIENT_ID_WEB',
  redirectUri: 'https://GIT_BRANCH.AMPLIFY_APP_ID.amplifyapp.com/',
  responseType: 'code',
  scope: 'phone email openid profile aws.cognito.signin.user.admin',
  showDebugInformation: !environment.production
};
