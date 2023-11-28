import { ChangeDetectionStrategy, Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { SelectCustomEvent, ToastController } from "@ionic/angular";
import { TaskService } from "../../services/task.service";
import { Task } from "../../model/task";
import { ZonedDate } from "../../../../shared/utils/dates/zoned-date";
import { StringUtils } from "../../../../shared/utils/string-utils";
import { ButtonsState } from "../../../../shared/utils/buttons/buttons-state";
import { ObjectUtils } from "../../../../shared/utils/object-utils";
import { ConsoleLogger } from 'aws-amplify/utils';
import { DeviceService } from "../../services/device.service";
import { Device } from "../../model/device";
import { TasksPagesEnum } from "../../routes/tasks-pages.enum";
import { ErrorPagesEnum } from "../../../errors/routes/error-pages.enum";
import { LoggerType } from "../../../../shared/utils/logger/logger-type";

@Component({
  selector: "city-tasks-save",
  templateUrl: "./save.component.html",
  styleUrls: ["./save.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SaveComponent extends ButtonsState implements OnInit {

  public taskPageTitle = "";
  public actualDateTime = "";
  public taskForm: FormGroup = this.formBuilder.group({
    name: [null, [Validators.required, Validators.minLength(5), Validators.maxLength(25)]],
    description: [null, [Validators.minLength(10), Validators.maxLength(150)]],
    deviceId: [null, Validators.required],  // Used only for Device selection.
    deviceList: [null, Validators.required],
    deviceOperation: [null, Validators.required],  // Used only for Device Operation selection.
    deviceOperationList: [null, Validators.required],
    hour: [null, [Validators.required, Validators.min(0), Validators.max(23)]],
    minute: [null, [Validators.required, Validators.min(0), Validators.max(59)]],
    executionTime: [null, Validators.required],  // Used only for Time 'HH:mm' formatting.
    executionDays: [null, Validators.required],
    executionDaysList: [null, Validators.required],  // Used only for Days of Week selection.
    executeUntil: [null]
  });
  public devices: Device[] = [];

  private logger: ConsoleLogger = new ConsoleLogger("SaveComponent", LoggerType.DEBUG);
  private originalTask: Task = {} as Task;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private taskService: TaskService,
              private deviceService: DeviceService,
              private formBuilder: FormBuilder,
              private toastController: ToastController) {
    super();
    this.actualDateTime = ZonedDate.getAsString(new Date());
  }

  public async ngOnInit(): Promise<void> {
    this.logger.debug("ngOnInit() - START");
    if (this.router.url.endsWith("create")) {
      this.taskPageTitle = "Create Task";
      super.setCreatingState();
    } else {
      this.taskPageTitle = "Edit Task";
      const taskId: string | null = this.route.snapshot.paramMap.get("taskId");
      if (StringUtils.isBlank(taskId)) {
        await this.presentToast(`Task ID required to present the data.`);
        await this.router.navigate([ErrorPagesEnum.notFoundPage]);
      } else {
        this.taskService.getByKey(taskId).subscribe((registeredTask: Task): void => {
          if (registeredTask) {
            this.originalTask = registeredTask;
            this.assignTaskValuesToForm(registeredTask);
          } else {
            this.presentToast(`Task <b>${taskId}</b> not found.`);
            this.router.navigate([ErrorPagesEnum.notFoundPage]);
          }
        });
      }
    }
    this.deviceService.getAll().subscribe((data: Device[]) => {
      this.devices = data;
    });
    this.logger.debug("ngOnInit() - END");
  }

  public formatExecutionTimeValue(selectedTimeValue: string | string[] | null | undefined): void {
    this.logger.debug("formatExecutionTimeValue() - START: ", selectedTimeValue);
    if (ObjectUtils.isNull(selectedTimeValue)) {
      return;
    }
    let timeValueAsString: string;
    if (Array.isArray(selectedTimeValue)) {
      timeValueAsString = selectedTimeValue.join(":");
    } else {
      timeValueAsString = selectedTimeValue as string;
    }
    if (StringUtils.isNotBlank(timeValueAsString)) {
      const executeUntilFormatted: string = ZonedDate.getParsed(timeValueAsString, "HH:mm");
      const taskTimeValues: string[] = executeUntilFormatted.split(":");
      this.taskForm.patchValue({
        hour: taskTimeValues[0]
      });
      this.taskForm.patchValue({
        minute: taskTimeValues[1]
      });
      this.taskForm.patchValue({
        executionTime: executeUntilFormatted
      });
    }
  }

  public deviceListChanged(event: Event): void {
    this.logger.debug("deviceListChanged() - START: ", event);
    const selectEvent: SelectCustomEvent = event as SelectCustomEvent;
    if (ObjectUtils.isNull(selectEvent.detail.value)) {
      return;
    }
    this.taskForm.patchValue({
      deviceId: selectEvent.detail.value
    });
  }

  public deviceOperationListChanged(event: Event): void {
    this.logger.debug("deviceOperationListChanged() - START: ", event);
    const selectEvent: SelectCustomEvent = event as SelectCustomEvent;
    if (ObjectUtils.isNull(selectEvent.detail.value)) {
      return;
    }
    this.taskForm.patchValue({
      deviceOperation: selectEvent.detail.value
    });
  }

  public executionDaysListChanged(event: Event): void {
    this.logger.debug("executionDaysListChanged() - START: ", event);
    const selectEvent: SelectCustomEvent = event as SelectCustomEvent;
    if (ObjectUtils.isNull(selectEvent.detail.value)) {
      return;
    }
    const executionDaysList: string[] = [];
    selectEvent.detail.value.forEach((day: string): void => {
      executionDaysList.push(day);
    });
    this.taskForm.patchValue({
      executionDays: executionDaysList.join(",")
    });
  }

  public formatExecuteUntilTimeValue(task: Task): void {
    if (ObjectUtils.isNotNull(task.executeUntil)) {
      const executeUntilDate: Date = new Date(task.executeUntil);
      task.executeUntil = ZonedDate.setTimeToMidnight(executeUntilDate).getTime();
    }
  }

  public formatExecuteUntilValue(selectedDateValue: string | string[] | null | undefined): void {
    this.logger.debug("formatExecuteUntilValue() - START: ", selectedDateValue);
    if (ObjectUtils.isNull(selectedDateValue)) {
      return;
    }
    let executionDateAsString: string;
    if (Array.isArray(selectedDateValue)) {
      executionDateAsString = selectedDateValue.join(":");
    } else {
      executionDateAsString = selectedDateValue as string;
    }
    if (StringUtils.isBlank(executionDateAsString)) {
      this.taskForm.patchValue({
        executeUntil: null
      });
    } else {
      this.taskForm.patchValue({
        executeUntil: ZonedDate.getParsed(executionDateAsString, "dd MMM yyyy")
      });
    }
  }

  public async save(): Promise<void> {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }
    if (super.isCreatingState()) {
      this.createTask();
    } else if (super.isUpdatingState()) {
      this.updateTask();
    }
    await this.router.navigateByUrl(TasksPagesEnum.tasksPage);
  }

  public async cancel(): Promise<void> {
    if (super.isCreatingState()) {
      this.taskForm.reset();
    } else if (super.isUpdatingState()) {
      this.taskForm.patchValue(this.originalTask);
      this.initDates(this.originalTask);
    }
    await this.router.navigateByUrl(TasksPagesEnum.tasksPage);
  }

  private assignTaskValuesToForm(registeredTask: Task): void {
    this.taskForm.patchValue(registeredTask);
    this.initDates(registeredTask);
    this.initList(registeredTask);
    super.setUpdatingState();
  }

  private initDates(registeredTask: Task): void {
    const executionTime: Date = new Date(1985, 4, 8, registeredTask.hour, registeredTask.minute, 0, 0);
    this.formatExecutionTimeValue(ZonedDate.getAsString(executionTime));
    if (ObjectUtils.isNotNull(registeredTask.executeUntil)) {
      const executionDate: Date = new Date(registeredTask.executeUntil);
      this.formatExecuteUntilValue(ZonedDate.getAsString(executionDate));
    }
  }

  private initList(registeredTask: Task): void {
    this.taskForm.patchValue({
      deviceId: registeredTask.deviceId
    });
    this.taskForm.patchValue({
      deviceOperation: registeredTask.deviceOperation
    });
    const executionDaysList: string[] = [];
    registeredTask.executionDays.split(",").forEach(day => {
      executionDaysList.push(day);
    });
    this.taskForm.patchValue({
      executionDaysList: executionDaysList
    });
  }

  private createTask(): void {
    const formValues = { ...this.taskForm.value };
    delete formValues.executionTime;
    delete formValues.deviceList;
    delete formValues.deviceOperationList
    delete formValues.executionDaysList;
    const newTask: Task = formValues;
    newTask.executionCommand = "python3 /home/pi/faker.py";
    this.formatExecuteUntilTimeValue(newTask);
    this.taskService.add(newTask).subscribe((createdTask: Task): void => {
      this.logger.debug("createTask() - Task created successfully: ", createdTask);
      this.presentToast("Task created successfully.").then((): void => {
        this.logger.debug("createTask() - Toast presented successfully.");
      });
    });
  }

  private updateTask(): void {
    const formValues = { ...this.taskForm.value };
    delete formValues.executionTime;
    delete formValues.deviceList;
    delete formValues.deviceOperationList
    delete formValues.executionDaysList;
    const updatedTask: Task = formValues;
    this.setUpdateImmutableValues(updatedTask);
    this.formatExecuteUntilTimeValue(updatedTask);
    this.taskService.update(updatedTask).subscribe((taskUpdated: Task): void => {
      this.logger.debug("updateTask() - Task updated successfully: ", taskUpdated);
      this.presentToast("Task updated successfully.").then((): void => {
        this.logger.debug("updateTask() - Toast presented successfully.");
      });
    });
  }

  private setUpdateImmutableValues(updatedTask: Task): void {
    updatedTask.id = this.originalTask.id;
    updatedTask.executionCommand = this.originalTask.executionCommand;
    updatedTask.createdAt = this.originalTask.createdAt;
  }

  private async presentToast(message: string): Promise<void> {
    const toast: HTMLIonToastElement = await this.toastController.create({
      message,
      duration: 3000
    });
    await toast.present();
  }
}
