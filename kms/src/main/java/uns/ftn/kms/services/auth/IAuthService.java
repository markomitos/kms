package uns.ftn.kms.services.auth;


import uns.ftn.kms.dtos.auth.requests.LoginRequest;
import uns.ftn.kms.dtos.auth.requests.RegisterRequest;
import uns.ftn.kms.dtos.auth.responses.LoginResponse;
import uns.ftn.kms.dtos.auth.responses.UserResponse;

public interface IAuthService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout();
}
