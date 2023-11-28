import { Injectable } from "@angular/core";
import { OAuthService } from "angular-oauth2-oidc";
import { Store } from "@ngrx/store";
import { selectUserFullName } from "../reactive/auth.selectors";
import { Observable } from "rxjs";
import { AuthActions } from "../reactive/action.types";

@Injectable({providedIn: 'root'})
export class AuthService {

  constructor(private oauthService: OAuthService, private store: Store) {
  }

  public initLoginFlow(): void {
    this.oauthService.initCodeFlow();
  }

  public async logOut(): Promise<void> {
    await this.oauthService.revokeTokenAndLogout();
    this.store.dispatch(AuthActions.logOutAction());
  }

  public isUserLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  public get accessToken(): string {
    return this.oauthService.getAccessToken();
  }

  public get username$(): Observable<string|undefined> {
    return this.store.select(selectUserFullName);
  }
}
