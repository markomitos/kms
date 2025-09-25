import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, UserResponse } from '../models/auth.model';
import { environment } from '../env/env';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  // Use the base URL from the environment file
  private readonly API_BASE_URL = `${environment.apiBaseUrl}/auth`;

  register(details: {
    email: string;
    password: string;
  }): Observable<UserResponse> {
    return this.http.post<UserResponse>(
      `${this.API_BASE_URL}/register`,
      details
    );
  }

  login(credentials: {
    email: string;
    password: string;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.API_BASE_URL}/login`, credentials)
      .pipe(
        // Use the 'tap' operator to perform a side effect (saving the token)
        tap((response) => this.saveToken(response.token))
      );
  }

  saveToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('authToken', token);
    }
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('authToken');
    }
  }

  getToken(): string | null | undefined {
    if (typeof localStorage !== 'undefined') {
      return localStorage.getItem('authToken');
    } else {
      return null
    }
  }
}
