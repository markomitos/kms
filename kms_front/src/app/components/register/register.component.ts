import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { passwordMatchValidator } from '../../validators/password-match.validator';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  isLoading = signal(false);
  message = signal<string | null>(null);
  messageType = signal<'success' | 'error'>('error');

  registerForm = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    },
    { validators: passwordMatchValidator }
  );

  onSubmit() {
    if (this.registerForm.invalid) {
      return;
    }
    this.isLoading.set(true);
    this.message.set(null);
    const { email, password } = this.registerForm.value;

    this.authService
      .register({ email: email!, password: password! })
      .subscribe({
        next: () => {
          this.messageType.set('success');
          this.message.set('Registration successful! Redirecting to login...');
          setTimeout(() => this.router.navigate(['/login']), 2000);
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

  switchToLogin() {
    this.router.navigate(['/login']);
  }
}
