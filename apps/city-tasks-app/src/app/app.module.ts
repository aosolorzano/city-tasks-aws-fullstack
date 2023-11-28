import { APP_INITIALIZER, NgModule } from "@angular/core";
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { RouteReuseStrategy } from "@angular/router";

import { BrowserModule } from "@angular/platform-browser";
import { IonicModule, IonicRouteStrategy } from "@ionic/angular";
import { AppRoutingModule } from "./app-routing.module";
import { AuthModule } from "./auth/auth.module";
import { OAuthModule, OAuthService } from "angular-oauth2-oidc";
import { initializeOAuth } from "./auth/providers/auth.provider";
import { Store, StoreModule } from "@ngrx/store";
import { EffectsModule } from "@ngrx/effects";
import { StoreRouterConnectingModule } from "@ngrx/router-store";
import { StoreDevtoolsModule } from "@ngrx/store-devtools";
import { HomePageModule } from "./pages/home/home.module";
import { TasksPageModule } from "./pages/tasks/tasks.module";
import { ErrorsPageModule } from "./pages/errors/errors.module";

import { SharedModule } from "./shared/shared.module";
import { AppComponent } from "./app.component";
import { AuthInterceptorService } from "./auth/interceptors/auth-interceptor.service";
import { environment } from "../environments/environment";
import { metaReducers, reducers } from "./app.reducers";

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    IonicModule.forRoot({}),
    OAuthModule.forRoot(),
    StoreModule.forRoot(reducers, {
      metaReducers,
      runtimeChecks: {
        strictStateImmutability: !environment.production,
        strictActionImmutability: !environment.production,
        strictStateSerializability: !environment.production,
        strictActionSerializability: !environment.production
      }
    }),
    EffectsModule.forRoot([]),
    StoreRouterConnectingModule.forRoot({}),
    environment.production ? [] : StoreDevtoolsModule.instrument({
      maxAge: 50,       // Retains last 50 states.
      logOnly: false,   // Restrict the extension to log-only mode.
      autoPause: true   // Pauses recording actions and state changes when the extension window is not open.
    }),
    AuthModule,
    SharedModule,
    HomePageModule,
    TasksPageModule,
    ErrorsPageModule
  ],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true },
    { provide: APP_INITIALIZER, useFactory: initializeOAuth, deps: [OAuthService, Store], multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
