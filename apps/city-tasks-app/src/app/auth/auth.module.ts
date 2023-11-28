import { NgModule } from "@angular/core";
import { authReducer } from "./auth.reducers";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { StoreModule } from "@ngrx/store";
import { EffectsModule } from "@ngrx/effects";

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    StoreModule.forFeature("auth", authReducer),
    EffectsModule.forFeature([])
  ],
  declarations: []
})
export class AuthModule {
}
