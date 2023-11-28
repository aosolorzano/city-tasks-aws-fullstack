import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'city-tasks-form-errors',
  templateUrl: './form-errors.component.html',
  styleUrls: ['./form-errors.component.scss'],
})
export class FormErrorsComponent {

  @Input() public field = '';
  @Input() public formGroup: FormGroup = new FormGroup({});

  public requiredMessage = '';
  public requiredMinLengthMessage = '';
  public requiredMaxLengthMessage = '';

  constructor() {
    //Nothing to implement.
  }

  public hasRequiredError(): boolean {
    const hasErrors = this.formGroup.controls[this.field].errors?.['required'] && this.formGroup.controls[this.field].touched;
    if (hasErrors && !this.requiredMessage) {
      this.requiredMessage = 'Field is required.';
    }
    return hasErrors;
  }

  public hasMinLengthError(): boolean {
    const hasErrors = this.formGroup.controls[this.field].errors?.['minlength'];
    if (hasErrors && !this.requiredMinLengthMessage) {
      this.requiredMinLengthMessage = `Field must be at least ${this.formGroup.controls[this.field].errors?.['minlength']} characters long.`;
    }
    return hasErrors;
  }

  public hasMaxLengthError(): boolean {
    const hasErrors = this.formGroup.controls[this.field].errors?.['maxlength'];
    if (hasErrors && !this.requiredMaxLengthMessage) {
      this.requiredMaxLengthMessage = `Field must be less than ${this.formGroup.controls[this.field].errors?.['maxlength']} characters long.`;
    }
    return hasErrors;
  }

}
