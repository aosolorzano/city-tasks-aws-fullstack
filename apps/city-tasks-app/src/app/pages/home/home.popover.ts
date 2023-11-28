import {Component} from '@angular/core';

import {PopoverController} from '@ionic/angular';

@Component({
  template: `
    <ion-list>
      <ion-item button (click)="close('https://aosolorzano.medium.com/')">
        <ion-label>Latest tutorials</ion-label>
      </ion-item>
      <ion-item button (click)="close('https://github.com/hiperium/hiperium-city-tasks')">
        <ion-label>GitHub Repo</ion-label>
      </ion-item>
      <ion-item button (click)="close('https://ionicframework.com/getting-started')">
        <ion-label>Learn Ionic</ion-label>
      </ion-item>
      <ion-item button (click)="close('https://showcase.ionicframework.com')">
        <ion-label>Ionic Showcase</ion-label>
      </ion-item>
    </ion-list>
  `
})
export class HomePopoverComponent {

  constructor(public popoverCtrl: PopoverController) {
  }

  public async close(url: string) {
    window.open(url, '_blank');
    await this.popoverCtrl.dismiss();
  }
}
