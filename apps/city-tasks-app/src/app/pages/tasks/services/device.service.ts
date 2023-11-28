import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Device } from "../model/device";
import { Observable } from "rxjs";

@Injectable()
export class DeviceService {

  constructor(public http: HttpClient) {
  }

  public getAll(): Observable<Device[]> {
    return this.http.get<Device[]>('assets/data/devices.json');
  }
}
