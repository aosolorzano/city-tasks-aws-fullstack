import { EntityMetadataMap, EntityDataModuleConfig } from '@ngrx/data';
import { compareTasks } from "./model/task";

const tasksEntityMetadata: EntityMetadataMap = {
  task: {
    sortComparer: compareTasks,
    entityDispatcherOptions: {
      optimisticAdd: false,
      optimisticUpdate: false,
      optimisticDelete: false
    }
  }
};

const pluralNames = {  };

export const tasksEntityConfig: EntityDataModuleConfig = {
  entityMetadata: tasksEntityMetadata,
  pluralNames
};
