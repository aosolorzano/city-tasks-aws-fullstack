import { Injectable } from "@angular/core";
import { DefaultDataService, HttpUrlGenerator } from "@ngrx/data";
import { HttpClient } from "@angular/common/http";
import { Task } from "../model/task";
import { TaskEntityEnum } from "../utils/task-entity.enum";
import { defaultDataServiceConfig } from "../../../../environments/environment";

@Injectable()
export class TaskDataService extends DefaultDataService<Task> {

  constructor(httpClient: HttpClient, httpUrlGenerator: HttpUrlGenerator) {
    super(TaskEntityEnum.task, httpClient, httpUrlGenerator, defaultDataServiceConfig);
  }
}
