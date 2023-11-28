import {ChangeDetectionStrategy, Component} from '@angular/core';
import {PopoverController} from '@ionic/angular';

@Component({
  selector: 'city-tasks-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SearchComponent {

  public searchOptions = [
    {title: 'Name', action: 'name'},
    {title: 'Day',  action: 'day'},
    {title: 'Hour', action: 'hour'}
  ];

  constructor(private popoverController: PopoverController) {}

  public async onSelectOption(option: string): Promise<void> {
    await this.popoverController.dismiss({
      optionSelected: option
    });
  }
}
