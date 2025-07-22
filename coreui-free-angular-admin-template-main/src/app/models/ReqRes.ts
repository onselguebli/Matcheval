export interface ReqRes {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
  dnaiss: string;
  civility: string;
  phonenumber: string;
  locked: boolean;
  enabled: boolean;
  role: string;
  statusCode?: number;
  message?: string;
}
