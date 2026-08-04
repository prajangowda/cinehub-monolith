package com.prajan.cinehub.auth.authService;



import com.prajan.cinehub.auth.dto.LoginResponse;
import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.enums.provider;
import com.prajan.cinehub.auth.model.CustomUserDetails;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OauthService {
    @Autowired
    private UserInRepository userRepo;

    @Autowired
    private JWTservice jwtservice;


    //OAuth2 method handling
    public LoginResponse handleOauthLoginRequest(OAuth2User oauth2User, String registrationId) {

        String email = oauth2User.getAttribute("email");

        provider providerType = jwtservice.getProviderFromRegistrationId(registrationId);

        String providerId = jwtservice.extractProviderId(oauth2User, registrationId);

        UserIn user = userRepo.findByProviderIdAndProvider(providerId, providerType).orElse(null);

        if ( user!= null && !user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account disabled by admin"
            );
        }

        if (user == null && email != null) {
            user = userRepo.findByEmail(email).orElse(null);
        }

        //new user
        if (user == null) {

            user = new UserIn();
            user.setEmail(email);
            user.setProviderId(providerId);
            user.setProvider(providerType);
            user.setRole(Role.USER); // important
            userRepo.save(user);
        }

        //user exits from normal sign up
        else if (user.getProviderId() == null) {

            throw new OAuth2AuthenticationException(
                    "login through other way"
            );
        }

        CustomUserDetails principal=new CustomUserDetails(user);
        return new LoginResponse(
                jwtservice.generateAccessToken(principal),
                jwtservice.generateRefreshToken(principal)
        );
    }
}
