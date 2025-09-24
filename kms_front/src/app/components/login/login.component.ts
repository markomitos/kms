import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  isLoading = signal(false);
  message = signal<string | null>(null);
  messageType = signal<'success' | 'error'>('error');

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }
    this.isLoading.set(true);
    this.message.set(null);

    this.authService.login(this.loginForm.value as any).subscribe({
      next: () => {
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.message.set(
          err.error?.message || err.message || 'An unknown error occurred.'
        );
        this.messageType.set('error');
        this.isLoading.set(false);
      },
      complete: () => {
        this.isLoading.set(false);
      },
    });
  }

  switchToRegister() {
    this.router.navigate(['/register']);
  }
}
