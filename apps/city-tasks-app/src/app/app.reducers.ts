import { Action, ActionReducer, ActionReducerMap, MetaReducer } from "@ngrx/store";
import { routerReducer } from "@ngrx/router-store";
import { environment } from "../environments/environment";
import { AppState } from "./app.state";

export const reducers: ActionReducerMap<AppState> = {
  router: routerReducer
};

const debugMetaReducer: MetaReducer<AppState> = (reducer: ActionReducer<AppState>): ActionReducer<AppState> => {
  return (state: AppState | undefined, action: Action): AppState => {
    const nextState: AppState = reducer(state, action);
    if (!action.type.endsWith("recompute")) {
      console.groupCollapsed(action.type);
      console.log("Previous state: ", state);
      console.log("Action", action);
      console.log("Next state: ", nextState);
      console.groupEnd();
    }
    return nextState;
  };
};

export const metaReducers: MetaReducer<AppState>[] = environment.production ? [] : [debugMetaReducer];
