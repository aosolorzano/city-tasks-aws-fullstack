import {createFeatureSelector, createSelector} from '@ngrx/store';
import { AuthState } from "../auth.state";

export const selectUserFeature = createFeatureSelector<AuthState>('auth');

export const selectUserFullName = createSelector(
  selectUserFeature,
  (state: AuthState) => state.user.info?.name
);
