import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ErrorsPage } from './errors.page';
import { ErrorPagesEnum } from "./routes/error-pages.enum";

const routes: Routes = [
  {
    path: ':errorCode',
    component: ErrorsPage,
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
export class ErrorsPageRoutingModule {}
