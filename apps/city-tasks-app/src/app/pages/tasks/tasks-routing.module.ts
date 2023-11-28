import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TasksPage } from './tasks.page';
import { SaveComponent } from "./components/save/save.component";
import { DetailsComponent } from "./components/details/details.component";
import { TasksRoutesEnum } from "./routes/tasks-routes.enum";
import { ErrorPagesEnum } from "../errors/routes/error-pages.enum";

const routes: Routes = [
  {
    path: '',
    component: TasksPage,
  },
  {
    path: TasksRoutesEnum.createRoute,
    component: SaveComponent
  },
  {
    path: TasksRoutesEnum.editRoute,
    component: SaveComponent
  },
  {
    path: TasksRoutesEnum.detailsRoute,
    component: DetailsComponent
  },
  {
    path: '**',
    redirectTo: ErrorPagesEnum.notFoundPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TasksPageRoutingModule {}
