import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'minutePipe'
})
export class MinutePipe implements PipeTransform {

  transform(minute: number): string {
    if (minute === 0) {
      return '00';
    }
    if (minute.toString().startsWith('0')) {
      return minute.toString();
    }
    if (minute < 10) {
      return '0' + minute;
    } else {
      return minute.toString();
    }
  }
}
