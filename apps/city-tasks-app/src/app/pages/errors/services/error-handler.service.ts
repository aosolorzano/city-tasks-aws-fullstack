import {Injectable, signal, WritableSignal} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import { ConsoleLogger } from 'aws-amplify/utils';
import { ErrorPagesEnum } from "../routes/error-pages.enum";
import { LoggerType } from "../../../shared/utils/logger/logger-type";

@Injectable({providedIn: 'root'})
export class ErrorHandlerService {

  public errorDetail: WritableSignal<string> = signal('');
  private logger:ConsoleLogger = new ConsoleLogger('ErrorHandlerService', LoggerType.DEBUG);

  constructor(private router: Router) {
  }

  public async handleHttpError(error: HttpErrorResponse): Promise<void> {
    this.logger.debug('handleHttpError(): ', error.status);
    this.errorDetail.set(error.message);
    switch (error.status) {
      case 401:
        await this.router.navigate([ErrorPagesEnum.unauthorizedPage]);
        break;
      case 403:
        await this.router.navigate([ErrorPagesEnum.forbiddenPage]);
        break;
      case 404:
        await this.router.navigate([ErrorPagesEnum.notFoundPage]);
        break;
      case 500:
        await this.router.navigate([ErrorPagesEnum.serverErrorPage]);
        break;
      case 503:
        await this.router.navigate([ErrorPagesEnum.serviceUnavailablePage]);
        break;
      default:
        await this.router.navigate([ErrorPagesEnum.serverErrorPage]);
    }
    // Thrown the error again to be caught by other subscribers.
    throw error;
  }
}
