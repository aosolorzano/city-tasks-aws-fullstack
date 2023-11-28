import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { TasksPageRoutingModule } from "./tasks-routing.module";
import { EffectsModule } from "@ngrx/effects";
import { StoreModule } from "@ngrx/store";
import { ReactiveFormsModule } from "@angular/forms";
import { SharedModule } from "../../shared/shared.module";
import { EntityDataModule, EntityDataService } from "@ngrx/data";

import { TaskDataService } from "./services/task-data.service";
import { TaskService } from "./services/task.service";
import { TasksPage } from "./tasks.page";
import { SaveComponent } from "./components/save/save.component";
import { DetailsComponent } from "./components/details/details.component";
import { SearchComponent } from "./components/search/search.component";

import { MinutePipe } from "./pipes/minute.pipe";
import { DaysPipe } from "./pipes/days.pipe";
import { SearchPipe } from "./pipes/search.pipe";
import { HourPipe } from "./pipes/hour.pipe";
import { TaskEntityEnum } from "./utils/task-entity.enum";
import { tasksEntityConfig } from "./tasks-entity-metadata";
import { DeviceService } from "./services/device.service";

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    ReactiveFormsModule,
    TasksPageRoutingModule,
    SharedModule,
    StoreModule.forFeature("tasks", []),
    EffectsModule.forFeature([]),
    EntityDataModule.forRoot(tasksEntityConfig),
  ],
  declarations: [
    TasksPage,
    SaveComponent,
    DetailsComponent,
    SearchComponent,
    DaysPipe,
    SearchPipe,
    HourPipe,
    MinutePipe,
  ],
  providers: [
    TaskService,
    TaskDataService,
    DeviceService
  ]
})
export class TasksPageModule {
  constructor(private entityDataService: EntityDataService,
              private tasksDataService: TaskDataService) {
    this.entityDataService.registerService(TaskEntityEnum.task, this.tasksDataService);
  }
}
