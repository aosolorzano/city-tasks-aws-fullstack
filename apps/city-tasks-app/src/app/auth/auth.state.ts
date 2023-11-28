import { User } from "./model/user";

export interface AuthState {
  user: User;
}

export const initialState: AuthState = {
  user: {} as User
};
