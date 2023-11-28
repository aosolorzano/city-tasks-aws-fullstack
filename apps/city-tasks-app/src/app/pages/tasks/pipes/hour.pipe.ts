import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'hourPipe'
})
export class HourPipe implements PipeTransform {

  transform(hour: number): string {
    if (hour === 0) {
      return '00';
    }
    if (hour.toString().startsWith('0')) {
      return hour.toString();
    }
    if (hour < 10) {
      return '0' + hour;
    } else {
      return hour.toString();
    }
  }
}
