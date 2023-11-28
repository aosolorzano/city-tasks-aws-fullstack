import { Injectable } from "@angular/core";
import { EntityCollectionServiceBase, EntityCollectionServiceElementsFactory } from "@ngrx/data";
import { Task } from "../model/task";
import { TaskEntityEnum } from "../utils/task-entity.enum";

@Injectable()
export class TaskService extends EntityCollectionServiceBase<Task> {

  constructor(serviceElementsFactory: EntityCollectionServiceElementsFactory) {
    super(TaskEntityEnum.task, serviceElementsFactory);
  }
}
