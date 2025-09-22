package uns.ftn.kms.services.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uns.ftn.kms.dtos.auth.requests.LoginRequest;
import uns.ftn.kms.dtos.auth.requests.RegisterRequest;
import uns.ftn.kms.dtos.auth.responses.LoginResponse;
import uns.ftn.kms.dtos.auth.responses.UserResponse;
import uns.ftn.kms.exceptions.InvalidCredentialsException;
import uns.ftn.kms.models.auth.User;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.models.auth.UserRole;
import uns.ftn.kms.repositories.IRootKeyRepository;
import uns.ftn.kms.repositories.IUserRepository;
import uns.ftn.kms.services.CryptographyService;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
@Service
@Primary
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtUtils;
    private final IRootKeyRepository rootKeyRepository;
    private final CryptographyService cryptographyService;
    private final ModelMapper mapper;

    @Transactional
    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidCredentialsException("Username is already taken");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);

        SecretKey rootKey = cryptographyService.generateAes256Key();
        rootKeyRepository.save(savedUser.getId().toString(), rootKey);

        return mapper.map(savedUser, UserResponse.class);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtUtils.generateAccessToken(userPrincipal));

            return loginResponse;

        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

}