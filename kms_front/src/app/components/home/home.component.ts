import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../env/env';
import {KeyType} from "../../models/keytype.model";
import {Key} from "../../models/key.model";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, // Required for *ngIf, *ngFor, json pipe
    FormsModule   // Required for [(ngModel)]
  ],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  // --- Injected Services ---
  private authService = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);
  private apiUrl = environment.apiBaseUrl;

  keys: Key[] = [];
  selectedKeyId: string | null = null;


  // --- FORM INPUTS ---
  createKeySize:number = 2048;
  createKeyType = 'SYMMETRIC_AES';
  opAliasS: string | null = null;
  opAliasAS: string | null = null ;
  symmAlgorithm: string | null = null;
  asymmAlgorithm: string | null = null;
  dataToProcess = 'Hello KMS!';
  signAlgorithm = 'SHA256withRSA';
  hmacAlgorithm = 'HmacSHA256';
  dataToSign = 'This data needs a signature!';
  dataToHmac = 'This data needs an HMAC!';

  // --- API RESPONSES (to display in UI) ---
  createKeyResponse: any;
  activeKeyActionResponse: any;
  generateDataKeyResponse: any;
  decryptDataKeyResponse: any;
  encryptAsymmetricResponse: any;
  decryptAsymmetricResponse: any;
  symmetricAlgorithms: any;
  asymmetricAlgorithms: any;
  signResponse: any;
  verifyResponse: any;
  generateHmacResponse: any;
  verifyHmacResponse: any;

  // --- Intermediate values for chained operations ---
  encryptedDataKey = '';
  encryptedAsymmetricData = '';
  generatedSignature = '';
  generatedHmac = '';
  signatureToVerify = '';
  hmacToVerify = '';

  ngOnInit(): void {
    // Fetch algorithms when the component loads
    this.getSymmetricAlgorithms();
    this.getAsymmetricAlgorithms();
    this.loadKeys();
  }

  loadKeys(): void {
    this.http.get<Key[]>(`${this.apiUrl}/keys`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (fetchedKeys) => {
          this.keys = fetchedKeys;
        },
        error: (err) => {
          console.error('Failed to load keys:', err);
        }
      });
  }

  // Helper to get auth headers (assumes token in localStorage)
  private getAuthHeaders(): HttpHeaders {
    // IMPORTANT: Your AuthService should ideally provide this.
    // For this PoC, we get the token directly.
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // === KEY CONTROLLER METHODS ===

  createKey(): void {
    const body = {
        keyType: this.createKeyType,
        keySize: this.createKeySize,

    };
    console.log(body)
    this.http.post(`${this.apiUrl}/keys`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => {this.createKeyResponse = res; this.keys.push(res);},

        error: err => this.createKeyResponse = err.error
      });
  }

  rotateKey(): void {
    if (!this.selectedKeyId) return;
    this.activeKeyActionResponse = null;
    this.http.post(`${this.apiUrl}/keys/${this.selectedKeyId}/rotate`, {}, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.activeKeyActionResponse = res,
        error: err => this.activeKeyActionResponse = err.error
      });
  }

  getKeyById(): void {
    if (!this.selectedKeyId) return;
    this.activeKeyActionResponse = null;
    this.http.get(`${this.apiUrl}/keys/${this.selectedKeyId}`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.activeKeyActionResponse = res,
        error: err => this.activeKeyActionResponse = err.error
      });
  }

  getActiveSymmetricKeyMaterial(): void {
    if (!this.selectedKeyId) return;
    this.activeKeyActionResponse = null;
    this.http.get(`${this.apiUrl}/keys/${this.selectedKeyId}/material`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.activeKeyActionResponse = res,
        error: err => this.activeKeyActionResponse = err.error
      });
  }

  getActivePublicKey(): void {
    if (!this.selectedKeyId) return;
    this.http.get(`${this.apiUrl}/keys/${this.selectedKeyId}/public-key`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.activeKeyActionResponse = res,
        error: err => this.activeKeyActionResponse = err.error
      });
  }

  // === CRYPTO CONTROLLER METHODS ===

  get symmetricKeys(): Key[] {
    return this.keys.filter(key => key.type?.toString() === "SYMMETRIC_AES");
  }

  get asymmetricKeys(): Key[] {
    return this.keys.filter(key => key.type?.toString() === "ASYMMETRIC_RSA");
  }


  generateDataKey(): void {
    const body = { alias: this.opAliasS, algorithm: this.symmAlgorithm };
    this.http.post(`${this.apiUrl}/crypto/generate-data-key`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res: any) => {
          this.generateDataKeyResponse = res;
          this.encryptedDataKey = res.encryptedKeyBase64; // Store for decryption
        },
        error: err => this.generateDataKeyResponse = err.error
      });
  }

  decryptDataKey(): void {
    const body = { alias: this.opAliasS, encryptedKeyBase64: this.encryptedDataKey };
    this.http.post(`${this.apiUrl}/crypto/decrypt-data-key`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.decryptDataKeyResponse = res,
        error: err => this.decryptDataKeyResponse = err.error
      });
  }

  encryptAsymmetric(): void {
    const dataBase64 = btoa(this.dataToProcess);
    const body = { alias: this.opAliasAS, dataBase64, algorithm: this.asymmAlgorithm };
    this.http.post(`${this.apiUrl}/crypto/encrypt-asymmetric`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res: any) => {
          this.encryptAsymmetricResponse = res;
          this.encryptedAsymmetricData = res.dataBase64;
        },
        error: err => this.encryptAsymmetricResponse = err.error
      });
  }

  decryptAsymmetric(): void {
    const body = { alias: this.opAliasAS, dataBase64: this.encryptedAsymmetricData };
    this.http.post(`${this.apiUrl}/crypto/decrypt-asymmetric`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res: any) => {
          // For display, convert Base64 response back to readable string
          try {
            res.decryptedText = atob(res.dataBase64);
          } catch (e) {
            res.decryptedText = 'Error decoding Base64 string.';
          }
          this.decryptAsymmetricResponse = res;
        },
        error: err => this.decryptAsymmetricResponse = err.error
      });
  }

  getSymmetricAlgorithms(): void {
    this.http.get(`${this.apiUrl}/crypto/algorithms/symmetric`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.symmetricAlgorithms = res,
        error: err => this.symmetricAlgorithms = err.error
      });
  }

  getAsymmetricAlgorithms(): void {
    this.http.get(`${this.apiUrl}/crypto/algorithms/asymmetric`, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.asymmetricAlgorithms = res,
        error: err => this.asymmetricAlgorithms = err.error
      });
  }

  // === SIGNING & HMAC METHODS ===

  signData(): void {
    const dataToSignBase64 = btoa(this.dataToSign);

    const body = {
      keyId: this.opAliasAS, // Koristimo selektor za asimetrične ključeve
      algorithm: this.signAlgorithm,
      dataToSignBase64: dataToSignBase64
    };

    this.http.post(`${this.apiUrl}/signing/sign`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res: any) => {
          this.signResponse = res;
          this.generatedSignature = res.signatureBase64;
        },
        error: err => this.signResponse = err.error
      });
  }

  verifySignature(): void {
    const originalDataBase64 = btoa(this.dataToSign);

    const body = {
      keyId: this.opAliasAS,
      algorithm: this.signAlgorithm,
      originalDataBase64: originalDataBase64,
      signatureBase64: this.generatedSignature
    };

    this.http.post(`${this.apiUrl}/signing/verify`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.verifyResponse = res,
        error: err => this.verifyResponse = err.error
      });
  }

  generateHmac(): void {
    const dataBase64 = btoa(this.dataToHmac);

    const body = {
      keyId: this.opAliasS,
      algorithm: this.hmacAlgorithm,
      dataBase64: dataBase64
    };

    this.http.post(`${this.apiUrl}/hmac/generate`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res: any) => {
          this.generateHmacResponse = res;
          this.generatedHmac = res.hmacBase64;
        },
        error: err => this.generateHmacResponse = err.error
      });
  }

  verifyHmac(): void {
    const dataBase64 = btoa(this.dataToHmac);

    const body = {
      keyId: this.opAliasS,
      algorithm: this.hmacAlgorithm,
      dataBase64: dataBase64,
      hmacBase64: this.generatedHmac
    };

    this.http.post(`${this.apiUrl}/hmac/verify`, body, { headers: this.getAuthHeaders() })
      .subscribe({
        next: res => this.verifyHmacResponse = res,
        error: err => this.verifyHmacResponse = err.error
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
