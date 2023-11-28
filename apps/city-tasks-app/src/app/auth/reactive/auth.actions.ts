import { createAction, props } from '@ngrx/store';
import { User } from "../model/user";

export const setUserProfileAction = createAction(
  '[AuthProvider] SET_USER_PROFILE',
  props<{ user: User }>()
);

export const logOutAction = createAction(
  '[AuthProvider] LOG_OUT'
);
