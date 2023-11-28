import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {IonicModule} from '@ionic/angular';
import {HeaderComponent} from './components/header/header.component';
import {FormErrorsComponent} from './components/form-errors/form-errors.component';
import {ErrorHandlerService} from "../pages/errors/services/error-handler.service";

@NgModule({
  imports: [
    CommonModule,
    IonicModule
  ],
  declarations: [
    HeaderComponent,
    FormErrorsComponent
  ],
  exports: [
    HeaderComponent,
    FormErrorsComponent,
  ],
  providers: [
    ErrorHandlerService
  ]
})
export class SharedModule {
}
