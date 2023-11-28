import { Component, OnInit } from "@angular/core";
import { Task } from "../../model/task";
import { ActivatedRoute, Router } from "@angular/router";
import { ToastController } from "@ionic/angular";
import { StringUtils } from "../../../../shared/utils/string-utils";
import { TaskService } from "../../services/task.service";
import { TasksPagesEnum } from "../../routes/tasks-pages.enum";
import { ErrorPagesEnum } from "../../../errors/routes/error-pages.enum";

@Component({
  selector: "city-tasks-details",
  templateUrl: "./details.component.html",
  styleUrls: ["./details.component.scss"]
})
export class DetailsComponent implements OnInit {

  public task: Task = {} as Task;
  public taskPageTitle = "Task Details";

  constructor(private route: ActivatedRoute,
              private router: Router,
              private toastController: ToastController,
              private taskService: TaskService) {
  }

  public async ngOnInit(): Promise<void> {
    const taskId: string | null = this.route.snapshot.paramMap.get("taskId");
    if (StringUtils.isBlank(taskId)) {
      await this.presentToast(`Task ID required to present the data.`);
      await this.router.navigate([ErrorPagesEnum.notFoundPage]);
    } else {
      this.taskService.getByKey(taskId).subscribe((registeredTask: Task): void => {
        if (registeredTask) {
          this.task = registeredTask;
        } else {
          this.presentToast(`Task <b>${taskId}</b> not found.`);
          this.router.navigate([ErrorPagesEnum.notFoundPage]);
        }
      });
    }
  }

  public async return(): Promise<void> {
    await this.router.navigateByUrl(TasksPagesEnum.tasksPage);
  }

  private async presentToast(message: string): Promise<void> {
    const toast: HTMLIonToastElement = await this.toastController.create({
      message,
      duration: 3000
    });
    await toast.present();
  }
}
