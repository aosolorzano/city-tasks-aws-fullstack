import { Component, OnInit, signal, WritableSignal } from "@angular/core";
import { Router } from "@angular/router";
import { Task } from "./model/task";
import { TaskService } from "./services/task.service";
import { AlertController, PopoverController, SearchbarCustomEvent, ToastController } from "@ionic/angular";
import { SearchComponent } from "./components/search/search.component";
import { ObjectUtils } from "../../shared/utils/object-utils";
import { StringUtils } from "../../shared/utils/string-utils";
import { ConsoleLogger } from 'aws-amplify/utils';
import { TasksPagesEnum } from "./routes/tasks-pages.enum";
import { LoggerType } from "../../shared/utils/logger/logger-type";

@Component({
  selector: "city-tasks-tasks",
  templateUrl: "./tasks.page.html",
  styleUrls: ["./tasks.page.scss"]
})
export class TasksPage implements OnInit {

  public searchText = "";
  public columnName = "name";
  public searchInputMode = "text";
  public tasks: WritableSignal<Task[]> = signal<Task[]>([]);
  public loaded: WritableSignal<boolean> = signal<boolean>(false);
  private logger: ConsoleLogger = new ConsoleLogger("TasksPage", LoggerType.DEBUG);

  constructor(private router: Router,
              private taskService: TaskService,
              private alertController: AlertController,
              private toastController: ToastController,
              private popoverController: PopoverController) {
  }

  public async ngOnInit(): Promise<void> {
    this.logger.debug("ngOnInit() - START");
    this.taskService.entities$.subscribe((tasks: Task[]): void => {
      this.tasks.set(tasks);
    });
    this.taskService.loaded$.subscribe((loaded: boolean): void => {
      this.loaded.set(loaded);
      if (!loaded) {
        this.taskService.getAll();
      }
    });
    this.logger.debug("ngOnInit() - END");
  }

  public async presentSearchOptionsPopover(event: Event): Promise<void> {
    const popover: HTMLIonPopoverElement = await this.popoverController.create({
      component: SearchComponent,
      animated: true,
      mode: "md",
      event,
      translucent: true,
      backdropDismiss: true
    });
    await popover.present();
    const { data } = await popover.onWillDismiss();
    if (data) {
      this.columnName = data.optionSelected;
      if (this.columnName === "hour") {
        this.searchInputMode = "numeric";
      } else {
        this.searchInputMode = "text";
      }
    }
  }

  public onSearchTextChange(event: Event): void {
    const searchBarEvent: SearchbarCustomEvent = event as SearchbarCustomEvent;
    if (ObjectUtils.isNull(searchBarEvent.detail.value)) {
      return;
    }
    const searchTextValue: string = searchBarEvent.detail.value as string;
    if (StringUtils.isNotBlank(searchTextValue)) {
      this.searchText = searchTextValue;
      this.logger.debug("onSearchTextChange(): " + this.searchText);
    }
  }

  public async create(): Promise<void> {
    await this.router.navigateByUrl(TasksPagesEnum.createTaskPage);
  }

  public async edit(taskId: number): Promise<void> {
    await this.router.navigate([TasksPagesEnum.editTaskPage, taskId]);
  }

  public async details(taskId: number): Promise<void> {
    await this.router.navigate([TasksPagesEnum.detailsTaskPage, taskId]);
  }

  public async presentDeleteTaskAlert(task: Task): Promise<void> {
    this.logger.debug("presentDeleteTaskAlert() - START: " + task.id);
    const alert: HTMLIonAlertElement = await this.alertController.create({
      header: "Delete Task",
      mode: "ios",
      backdropDismiss: false,
      message: "Are you sure you want to delete this task and its corresponding timer job?",
      buttons: [
        {
          text: "Cancel",
          handler: (): void => {
            this.logger.debug("Cancel task deletion.");
          }
        }, {
          text: "Yes",
          handler: (): void => {
            this.delete(task);
          }
        }]
    });
    await alert.present();
  }

  private async delete(task: Task): Promise<void> {
    this.logger.debug("delete() - START: " + task.id);
    this.taskService.delete(task.id);
    await this.presentToast();
    this.logger.debug("delete() - END");
  }

  private async presentToast(): Promise<void> {
    const toast: HTMLIonToastElement = await this.toastController.create({
      message: "Task deleted successfully.",
      duration: 2000
    });
    await toast.present();
  }
}
