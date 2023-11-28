import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'daysPipe'
})
export class DaysPipe implements PipeTransform {

  transform(daysString: string, endIndex = 0): string {
    if (!daysString || daysString.length === 0) {
      return '';
    }
    const dayList: string[] = daysString.split(',');
    const newDaysList = dayList.map(day => this.getDayName(day, endIndex));
    return newDaysList.join(', ');
  }

  private getDayName(value: string, endIndex: number): string {
    let name;
    switch (value) {
      case 'MON':
        name = endIndex === 0 ? 'Monday' : 'Monday'.substring(0, endIndex);
        break;
      case 'TUE':
        name = endIndex === 0 ? 'Tuesday' : 'Tuesday'.substring(0, endIndex);
        break;
      case 'WED':
        name = endIndex === 0 ? 'Wednesday' : 'Wednesday'.substring(0, endIndex);
        break;
      case 'THU':
        name = endIndex === 0 ? 'Thursday' : 'Thursday'.substring(0, endIndex);
        break;
      case 'FRI':
        name = endIndex === 0 ? 'Friday' : 'Friday'.substring(0, endIndex);
        break;
      case 'SAT':
        name = endIndex === 0 ? 'Saturday' : 'Saturday'.substring(0, endIndex);
        break;
      case 'SUN':
        name = endIndex === 0 ? 'Sunday' : 'Sunday'.substring(0, endIndex);
        break;
      default:
        name = '';
    }
    return name;
  }
}
