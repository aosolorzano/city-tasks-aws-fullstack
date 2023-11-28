import { Pipe, PipeTransform } from '@angular/core';
import { Task } from '../model/task';

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {

  private numberRegex = /^\d*$/;

  transform(arrayValues: Task[], textToSearch = '', column = 'name'): Task[] {
    if (textToSearch === '') {
      return arrayValues;
    }
    if (!arrayValues) {
      return arrayValues;
    }
    let result: Task[] = [];
    switch (column) {
      case 'name':
        result = arrayValues.filter(task =>
          task.name.toLowerCase().includes(textToSearch.toLowerCase())
        );
        break;
      case 'day':
        result = arrayValues.filter(task =>
          task.executionDays.toLowerCase().includes(textToSearch.toLowerCase())
        );
        break;
      case 'hour':
        if (!this.numberRegex.test(textToSearch)) {
          alert('Please provide a valid Hour number.');
          return arrayValues;
        }
        result = arrayValues.filter(task =>
          task.hour === parseInt(textToSearch, 10)
        );
        break;
    }
    return result;
  }
}
