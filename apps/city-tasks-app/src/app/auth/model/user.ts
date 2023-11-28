export interface User {
  info: {
    sub: string;
    aud: string;
    email: string;
    email_verified: boolean;
    name: string;
  };
}
