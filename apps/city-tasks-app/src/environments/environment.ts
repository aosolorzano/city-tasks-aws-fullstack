import { AuthConfig } from 'angular-oauth2-oidc';
import { DefaultDataServiceConfig } from '@ngrx/data';

export const environment = {
  production: false,
  timeZone: 'America/Guayaquil'
};

export const defaultDataServiceConfig: DefaultDataServiceConfig = {
  root: 'https://city-t-Publi-GqQUR9xpnuXb-1763337452.us-east-1.elb.amazonaws.com/api/v1',
  timeout: 3000,
  delete404OK: false,
  trailingSlashEndpoints: false
};

export const authConfig: AuthConfig = {
  issuer: 'https://cognito-idp.us-east-1.amazonaws.com/us-east-1_cyHEaJxty',
  oidc: true,
  strictDiscoveryDocumentValidation: false,
  clientId: '6rmsact7vuhhlks06beu761sea',
  redirectUri: 'https://dev.d1mkw71bxsv92v.amplifyapp.com/',
  responseType: 'code',
  scope: 'phone email openid profile aws.cognito.signin.user.admin',
  showDebugInformation: !environment.production
};
