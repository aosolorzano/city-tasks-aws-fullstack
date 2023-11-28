import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { HomePageRoutingModule } from "./home-routing.module";
import { HomePage } from "./home.page";
import { HomePopoverComponent } from "./home.popover";

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    HomePageRoutingModule
  ],
  declarations: [HomePage, HomePopoverComponent],
})
export class HomePageModule {}
