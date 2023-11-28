import { Component, OnInit, signal, Signal, WritableSignal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { Event, NavigationEnd, NavigationStart, Router } from "@angular/router";
import { AuthService } from "./auth/services/auth.service";
import { LoadingService } from "./shared/services/loading.service";
import { ConsoleLogger } from "aws-amplify/utils";
import { LoggerType } from "./shared/utils/logger/logger-type";

@Component({
  selector: "city-tasks-app-root",
  templateUrl: "app.component.html",
  styleUrls: ["app.component.scss"]
})
export class AppComponent implements OnInit {

  public appPages = [
    { title: "Tasks", url: "/tasks", icon: "calendar-number" }
  ];
  public isUserLoggedIn = false;
  public username: Signal<string | undefined> = toSignal(this.authService.username$);
  public loading: WritableSignal<boolean> = signal<boolean>(true);
  private logger: ConsoleLogger = new ConsoleLogger("AppComponent", LoggerType.DEBUG);

  constructor(private router: Router,
              private authService: AuthService,
              private loadingService: LoadingService) {
  }

  public async ngOnInit(): Promise<void> {
    this.isUserLoggedIn = this.authService.isUserLoggedIn();
    this.loadingService.loading$.subscribe((loading: boolean): void => {
      this.setLoadingStatusFromService(loading);
    });
    this.router.events.subscribe((event: Event) => {
      this.setLoadingStatusFromEvent(event);
    });
  }

  public async login(): Promise<void> {
    this.authService.initLoginFlow();
  }

  public async logout(): Promise<void> {
    this.logger.debug('logout() - BEGIN');
    await this.authService.logOut();
    this.isUserLoggedIn = false;
    await this.router.navigate(['/']);
    this.logger.debug('logout() - END');
  }

  private setLoadingStatusFromService(loading: boolean): void {
    if (loading) {
      this.loading.set(true);
    } else {
      this.loading.set(false);
    }
  }

  private setLoadingStatusFromEvent(event: Event): void {
    if (event instanceof NavigationStart) {
      this.logger.debug("Navigation Start.");
      this.loadingService.show();
    } else if (event instanceof NavigationEnd) {
      this.logger.debug("Navigation End.");
      this.loadingService.hide();
    }
  }
}
