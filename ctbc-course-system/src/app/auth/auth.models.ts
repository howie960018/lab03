

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
}