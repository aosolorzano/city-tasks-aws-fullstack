import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { SharedModule } from "../../shared/shared.module";
import { ErrorsPageRoutingModule } from './errors-routing.module';
import { ErrorsPage } from './errors.page';

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    ErrorsPageRoutingModule,
    SharedModule
  ],
  declarations: [
    ErrorsPage
  ],
})
export class ErrorsPageModule {}
