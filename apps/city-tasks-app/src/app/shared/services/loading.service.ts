import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class LoadingService {

  private _loading: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  public show(): void {
    this._loading.next(true);
  }

  public hide(): void {
    this._loading.next(false);
  }

  public get loading$(): Observable<boolean> {
    return this._loading.asObservable();
  }
}
