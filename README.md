# Mini KMS (Key Management Service)
Mini KMS is a simplified implementation of a Key Management Service, inspired by cloud solutions like AWS KMS. It provides a centralized REST API for managing the lifecycle of cryptographic keys and performing common cryptographic operations in a secure manner.

This project was developed with a focus on secure software development practices, including a secure storage mechanism, access control, and automated code analysis.

## Core Features
- Key Lifecycle Management: Create, rotate, and retrieve both symmetric (AES) and asymmetric (RSA) keys.

- Key Versioning: Automatically versions keys upon rotation, preserving old material for decrypting legacy data.

- Cryptographic Services:

  * Encryption/Decryption: Perform cryptographic operations using managed symmetric and asymmetric keys.

  *  Digital Signatures: Sign data and verify signatures using RSA key pairs.

  * HMAC Generation: Create and verify Hash-based Message Authentication Codes (HMACs) for data integrity.

- Secure Storage: Employs a root key concept to encrypt all key material at rest in a PostgreSQL database.

- Access Control: Secured endpoints with a basic role-based access control system using JWTs.

- Static Code Analysis: Integrated with GitHub CodeQL to automatically scan for security vulnerabilities.

## Technology Stack
- Backend: Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Maven

- Frontend: TypeScript, Angular, Tailwind

## Prerequisites
Before you begin, ensure you have the following installed:

- JDK 17 or later

- Maven 3.6+

- Node.js and npm

- Angular CLI (npm install -g @angular/cli)

- A running PostgreSQL instance

- An API client like Postman (recommended for testing)

## Setup and Running
The project is divided into a backend (Spring Boot) and a frontend (Angular).

### Backend Setup
   Navigate to the backend project directory kms.

1. Create a PostgreSQL database:

    ```
    CREATE DATABASE mini_kms;
    ```

2. Configure the application:

   - Open src/main/resources/application.properties.

   - Update the spring.datasource.url, spring.datasource.username, and spring.datasource.password properties to match your PostgreSQL setup.

3. the Root Key: The application requires an environment variable for the master encryption key. Set it before running:

   - On Linux/macOS 

       ```
       export KMS_ROOT_KEY="your-super-secret-and-long-root-key"
       ```
   - On Windows (Command Prompt)

       ```
       set KMS_ROOT_KEY="your-super-secret-and-long-root-key"
       ```

4. Run the application:

The backend will be running on http://localhost:8080.

## Frontend Setup
1. Navigate to the frontend project directory kms_front.

2. Install dependencies:

    ```
    npm install
   ```

3. Run the application:

    ```
    ng serve
    ```

4. Access the application by opening your browser to http://localhost:4200.

## API Overview
The backend exposes a REST API for all operations.

- Key Management (/api/keys): Endpoints for creating, rotating, and retrieving key metadata.

- Cryptographic Operations (/api/crypto): Endpoints for encryption, decryption, and data key generation.

- Signing Operations (/api/signing): Endpoints for creating and verifying digital signatures.

- HMAC Operations (/api/hmac): Endpoints for generating and verifying HMAC codes.

This project was developed as part of a secure software development course.