import {Component, Input} from '@angular/core';

@Component({
  selector: 'city-tasks-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  @Input() pageTitle: string | undefined;
  @Input() showBackButton: boolean | undefined;

  constructor() {
    //Nothing to implement.
  }

}
