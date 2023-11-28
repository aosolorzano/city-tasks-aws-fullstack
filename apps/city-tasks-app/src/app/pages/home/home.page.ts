import { Component } from '@angular/core';
import { PopoverController } from "@ionic/angular";
import { HomePopoverComponent } from "./home.popover";

@Component({
  selector: 'city-tasks-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage {

  constructor(public popoverCtrl: PopoverController) { }

  public async presentPopover(event: Event): Promise<void> {
    const popover: HTMLIonPopoverElement = await this.popoverCtrl.create({
      component: HomePopoverComponent,
      event
    });
    await popover.present();
  }
}
