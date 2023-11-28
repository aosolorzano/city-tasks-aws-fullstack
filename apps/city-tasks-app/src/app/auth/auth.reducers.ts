import { createReducer, on } from "@ngrx/store";
import { AuthActions } from "./reactive/action.types";
import { AuthState, initialState } from "./auth.state";

export const authReducer = createReducer(
  initialState,
  on(AuthActions.setUserProfileAction,
    (state: AuthState, action): AuthState => {
    return {
      ...state,
      user: action.user
    };
  }),
  on(AuthActions.logOutAction,
    (): AuthState => {
    return initialState;
  })
);
