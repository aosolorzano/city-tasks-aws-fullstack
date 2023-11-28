import { AuthActions } from "../reactive/action.types";
import { authConfig } from "../../../environments/environment";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { User } from "../model/user";
import { OAuthService } from "angular-oauth2-oidc";
import { AuthService } from "../services/auth.service";
import { ConsoleLogger } from 'aws-amplify/utils';
import { LoggerType } from "../../shared/utils/logger/logger-type";
import { ErrorPagesEnum } from "../../pages/errors/routes/error-pages.enum";

const logger: ConsoleLogger = new ConsoleLogger("AuthProvider", LoggerType.DEBUG);

export function initializeOAuth(oauthService: OAuthService, store: Store): () => Promise<void> {
  return async (): Promise<void> => {
    logger.debug("initializeOAuth() - START");
    oauthService.configure(authConfig);
    await oauthService.loadDiscoveryDocumentAndTryLogin();
    logger.debug("Discovery document loaded.");

    if (oauthService.hasValidAccessToken()) {
      logger.debug("User Logged In.");
      const userProfile: NonNullable<unknown> = await oauthService.loadUserProfile();
      const user: User = userProfile as User;
      store.dispatch(AuthActions.setUserProfileAction({ user }));
    } else {
      logger.debug("User Not Logged In.");
    }
  };
}

export const authGuard = async (): Promise<boolean> => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);
  const isUserLoggedIn: boolean = authService.isUserLoggedIn();

  if (isUserLoggedIn) {
    logger.debug("User is logged in.");
  } else {
    logger.debug("User is not logged in. Redirecting to Unauthorized page.");
    await router.navigate([ErrorPagesEnum.unauthorizedPage]);
  }
  return isUserLoggedIn;
};
