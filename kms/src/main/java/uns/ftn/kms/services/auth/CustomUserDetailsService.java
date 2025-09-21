package uns.ftn.kms.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import uns.ftn.kms.exceptions.UserNotFoundException;
import uns.ftn.kms.models.auth.User;
import uns.ftn.kms.models.auth.UserPrincipal;
import uns.ftn.kms.repositories.IUserRepository;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return new UserPrincipal(user);
    }
}