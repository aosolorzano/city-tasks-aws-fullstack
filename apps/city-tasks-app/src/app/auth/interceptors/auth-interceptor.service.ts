import { Injectable } from "@angular/core";
import { catchError, Observable, retry } from "rxjs";
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { ErrorHandlerService } from "../../pages/errors/services/error-handler.service";
import { ConsoleLogger } from 'aws-amplify/utils';
import { LoggerType } from "../../shared/utils/logger/logger-type";

@Injectable({ providedIn: "root" })
export class AuthInterceptorService implements HttpInterceptor {

  private readonly authorizationHeader = "Authorization";
  private logger: ConsoleLogger = new ConsoleLogger("AuthInterceptorService", LoggerType.DEBUG);

  constructor(private errorHandlerService: ErrorHandlerService,
              private authService: AuthService) {
  }

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.logger.debug("START: ", request.url);
    request = this.setAuthorizationHeader(request);
    request = this.removeTrailingSlashes(request);
    return next.handle(request).pipe(
      retry({ count: 2, delay: 500, resetOnSuccess: true }),
      catchError(async (error: HttpErrorResponse) => {
        await this.errorHandlerService.handleHttpError(error);
        throw error;
      })
    );
  }

  private setAuthorizationHeader(request: HttpRequest<any>): HttpRequest<any> {
    const token: string = this.authService.accessToken;
    if (token) {
      request = request.clone({
        headers: request.headers.set(this.authorizationHeader, `Bearer ${token}`)
      });
    } else if (request.headers.has(this.authorizationHeader)) {
      this.logger.debug("No token found. Removing JWT from request header.");
      request = request.clone({
        headers: request.headers.delete(this.authorizationHeader)
      });
    }
    return request;
  }

  private removeTrailingSlashes(request: HttpRequest<any>) {
    if (request.url.endsWith('/')) {
      const url = request.url.slice(0, -1);
      request = request.clone({ url });
    }
    return request;
  }
}
