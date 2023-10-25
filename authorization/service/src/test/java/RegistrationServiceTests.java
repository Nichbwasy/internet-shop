import com.shop.authorization.common.constant.UsersRoles;
import com.shop.authorization.dao.RoleRepository;
import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.model.Role;
import com.shop.authorization.model.UserData;
import com.shop.authorization.model.UserRefreshToken;
import com.shop.authorization.service.exception.jwt.provider.AccessTokenGenerationException;
import com.shop.authorization.service.exception.registration.EmailAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.LoginAlreadyExistsRegistrationException;
import com.shop.authorization.service.exception.registration.RefreshTokenSavingRegistrationException;
import com.shop.authorization.service.exception.registration.UserSavingRegistrationException;
import com.shop.authorization.service.impl.RegistrationServiceImpl;
import com.shop.authorization.service.jwt.provider.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RegistrationServiceTests {

    private final Role USER_ROLE = new Role(1L, UsersRoles.USER, null);

    @Mock
    private UserDataRepository userDataRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRefreshTokenRepository userRefreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    /** REGISTRATION METHOD TESTS **/

    @Test
    public void registerTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(false);
        Mockito.when(userDataRepository.existsByEmail("user@somemail.com")).thenReturn(false);
        Mockito.when(roleRepository.getByName(UsersRoles.USER)).thenReturn(USER_ROLE);
        Mockito.when(userDataRepository.save(Mockito.any(UserData.class)))
                .thenAnswer(a -> a.getArguments()[0]);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("refreshToken");
        Mockito.when(userRefreshTokenRepository.save(Mockito.any(UserRefreshToken.class)))
                .thenAnswer(a -> a.getArguments()[0]);

        AccessRefreshTokens tokens = registrationService.registerUser(form);

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals("accessToken", tokens.getAccessToken());
        Assertions.assertEquals("refreshToken", tokens.getRefreshToken());
    }

    @Test
    public void registerDatabaseExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(false);
        Mockito.when(userDataRepository.existsByEmail("user@somemail.com")).thenReturn(false);
        Mockito.when(roleRepository.getByName(UsersRoles.USER)).thenReturn(USER_ROLE);
        Mockito.when(userDataRepository.save(Mockito.any(UserData.class)))
                .thenAnswer(a -> a.getArguments()[0]);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("refreshToken");
        Mockito.when(userRefreshTokenRepository.save(Mockito.any(UserRefreshToken.class)))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(
                RefreshTokenSavingRegistrationException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerAccessTokenGenerationExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(false);
        Mockito.when(userDataRepository.existsByEmail("user@somemail.com")).thenReturn(false);
        Mockito.when(roleRepository.getByName(UsersRoles.USER)).thenReturn(USER_ROLE);
        Mockito.when(userDataRepository.save(Mockito.any(UserData.class)))
                .thenAnswer(a -> a.getArguments()[0]);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class)))
                .thenThrow(AccessTokenGenerationException.class);

        Assertions.assertThrows(
                AccessTokenGenerationException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerUsersRoleNotFoundExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(false);
        Mockito.when(userDataRepository.existsByEmail("user@somemail.com")).thenReturn(false);
        Mockito.when(roleRepository.getByName(UsersRoles.USER))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                UserSavingRegistrationException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerEmailAlreadyExistsExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(false);
        Mockito.when(userDataRepository.existsByEmail("user@somemail.com")).thenReturn(true);

        Assertions.assertThrows(
                EmailAlreadyExistsRegistrationException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerLoginAlreadyExistsExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                "user", "user@somemail.com",
                "pass", "pass");

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(true);

        Assertions.assertThrows(
                LoginAlreadyExistsRegistrationException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerFormWithNullDataExceptionTest() {
        RegistrationForm form = new RegistrationForm(
                null, null,
                null, null);

        Mockito.when(userDataRepository.existsByLogin("user")).thenReturn(true);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> registrationService.registerUser(form)
        );
    }

    @Test
    public void registerNullFormExceptionTest() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> registrationService.registerUser(null)
        );
    }

}
