import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.auth.AuthorizationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.model.UserData;
import com.shop.authorization.model.UserRefreshToken;
import com.shop.authorization.service.encoder.PasswordEncoder;
import com.shop.authorization.service.exception.authorization.UserNotFoundAuthorizationException;
import com.shop.authorization.service.exception.jwt.provider.AccessTokenGenerationException;
import com.shop.authorization.service.impl.AuthorizationServiceImpl;
import com.shop.authorization.service.jwt.provider.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthorizationServiceTests {

    @Mock
    private UserDataRepository userDataRepository;
    @Mock
    private UserRefreshTokenRepository userRefreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    /** AUTHORIZE USER METHOD TESTS **/

    @Test
    public void authorizeTest() {
        AuthorizationForm form = new AuthorizationForm("user", "user");
        UserData user = new UserData();
        user.setId(1L);
        user.setLogin("user");
        user.setEmail("user@somemail.com");
        user.setPassword(PasswordEncoder.encode("user"));

        Mockito.when(userDataRepository.existsByLoginOrEmail("user", "user")).thenReturn(true);
        Mockito.when(userDataRepository.getByLoginOrEmail("user", "user")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("refreshToken");
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(1L)).thenReturn(new UserRefreshToken());

        AccessRefreshTokens tokens = authorizationService.authorizeUser(form);

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals("accessToken", tokens.getAccessToken());
        Assertions.assertEquals("refreshToken", tokens.getRefreshToken());
    }

    @Test
    public void authorizeDatabaseExceptionTest() {
        AuthorizationForm form = new AuthorizationForm("user", "user");
        UserData user = new UserData();
        user.setId(1L);
        user.setLogin("user");
        user.setEmail("user@somemail.com");
        user.setPassword(PasswordEncoder.encode("user"));

        Mockito.when(userDataRepository.existsByLoginOrEmail("user", "user")).thenReturn(true);
        Mockito.when(userDataRepository.getByLoginOrEmail("user", "user")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("refreshToken");
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(1L)).
                thenThrow(RuntimeException.class);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> authorizationService.authorizeUser(form)
        );
    }

    @Test
    public void authorizeUserNotExistsExceptionTest() {
        AuthorizationForm form = new AuthorizationForm("user", "user");
        UserData user = new UserData();
        user.setId(1L);
        user.setLogin("user");
        user.setEmail("user@somemail.com");
        user.setPassword(PasswordEncoder.encode("user"));

        Mockito.when(userDataRepository.existsByLoginOrEmail("user", "user")).thenReturn(true);
        Mockito.when(userDataRepository.getByLoginOrEmail("user", "user")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("accessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("refreshToken");
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(false);
        Mockito.when(userRefreshTokenRepository.save(Mockito.any()))
                .thenAnswer(a -> a.getArguments()[0]);

        AccessRefreshTokens tokens = authorizationService.authorizeUser(form);

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals("accessToken", tokens.getAccessToken());
        Assertions.assertEquals("refreshToken", tokens.getRefreshToken());
    }

    @Test
    public void authorizeAccessTokenGenerationExceptionTest() {
        AuthorizationForm form = new AuthorizationForm("user", "user");
        UserData user = new UserData();
        user.setId(1L);
        user.setLogin("user");
        user.setEmail("user@somemail.com");
        user.setPassword(PasswordEncoder.encode("user"));

        Mockito.when(userDataRepository.existsByLoginOrEmail("user", "user")).thenReturn(true);
        Mockito.when(userDataRepository.getByLoginOrEmail("user", "user")).thenReturn(user);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class)))
                .thenThrow(AccessTokenGenerationException.class);

        Assertions.assertThrows(
                AccessTokenGenerationException.class,
                () -> authorizationService.authorizeUser(form)
        );
    }

    @Test
    public void authorizeUserNotFoundExceptionTest() {
        AuthorizationForm form = new AuthorizationForm("user", "user");

        Mockito.when(userDataRepository
                .existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(false);

        Assertions.assertThrows(
                UserNotFoundAuthorizationException.class,
                () -> authorizationService.authorizeUser(form)
        );
    }

    @Test
    public void authorizeNullFormDataExceptionTest() {
        AuthorizationForm form = new AuthorizationForm(null, null);

        Mockito.when(userDataRepository
                        .existsByLoginOrEmail(
                                Mockito.nullable(String.class),
                                Mockito.nullable(String.class)))
                .thenThrow(NullPointerException.class);

        Assertions.assertThrows(
                NullPointerException.class,
                () -> authorizationService.authorizeUser(form)
        );
    }

    @Test
    public void authorizeNullFormExceptionTest() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> authorizationService.authorizeUser(null)
        );
    }

}
