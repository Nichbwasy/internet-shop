import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.model.UserData;
import com.shop.authorization.model.UserRefreshToken;
import com.shop.authorization.service.exception.jwt.provider.AccessTokenGenerationException;
import com.shop.authorization.service.exception.jwt.util.JwtParceClaimsException;
import com.shop.authorization.service.exception.token.TokensNotMatchException;
import com.shop.authorization.service.exception.token.UserNotFoundTokensServiceException;
import com.shop.authorization.service.impl.TokensServiceImpl;
import com.shop.authorization.service.jwt.provider.JwtTokenProvider;
import com.shop.authorization.service.jwt.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
public class TokenServiceTests {

    private final String REFRESH_TOKEN_SAMPLE = "some_refresh_token";
    @Mock
    private UserRefreshTokenRepository userRefreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private TokensServiceImpl tokensService;

    /** REFRESH TOKEN METHOD TESTS **/

    @Test
    public void refreshTokenTest() {
        UserRefreshToken userRefreshToken = new UserRefreshToken(1L, new UserData(), REFRESH_TOKEN_SAMPLE);
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(REFRESH_TOKEN_SAMPLE))
                .thenReturn(1L);
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(1L)).thenReturn(userRefreshToken);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class))).thenReturn("newAccessToken");
        Mockito.when(jwtTokenProvider.generateRefreshToken(Mockito.any(UserData.class))).thenReturn("newRefreshToken");

        AccessRefreshTokens tokens = tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE);

        Assertions.assertNotNull(tokens);
        Assertions.assertEquals("newAccessToken", tokens.getAccessToken());
        Assertions.assertEquals("newRefreshToken", tokens.getRefreshToken());
    }

    @Test
    public void refreshTokenGenerateAccessTokenExceptionTest() {
        UserRefreshToken userRefreshToken = new UserRefreshToken(1L, new UserData(), REFRESH_TOKEN_SAMPLE);
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(REFRESH_TOKEN_SAMPLE))
                .thenReturn(1L);
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(1L)).thenReturn(userRefreshToken);
        Mockito.when(jwtTokenProvider.generateAccessToken(Mockito.any(UserData.class)))
                .thenThrow(AccessTokenGenerationException.class);

        Assertions.assertThrows(
                AccessTokenGenerationException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }

    @Test
    public void refreshTokenTokensNotMatchExceptionExceptionTest() {
        UserRefreshToken userRefreshToken = new UserRefreshToken(1L, new UserData(), "WRONG_TOKEN");
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(REFRESH_TOKEN_SAMPLE))
                .thenReturn(1L);
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(1L)).thenReturn(userRefreshToken);

        Assertions.assertThrows(
                TokensNotMatchException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }

    @Test
    public void refreshTokenDatabaseExceptionTest() {
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(REFRESH_TOKEN_SAMPLE))
                .thenReturn(1L);
        Mockito.when(userRefreshTokenRepository.existsByUserId(1L)).thenReturn(true);
        Mockito.when(userRefreshTokenRepository.getByUserId(Mockito.anyLong()))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(
                RuntimeException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }

    @Test
    public void refreshTokenUserNotExistsExceptionTest() {
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(REFRESH_TOKEN_SAMPLE))
                .thenReturn(1L);
        Mockito.when(userRefreshTokenRepository.existsByUserId(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(
                UserNotFoundTokensServiceException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }

    @Test
    public void refreshTokenGetUserIdFromTokenExceptionTest() {
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(Mockito.anyString()))
                .thenThrow(JwtParceClaimsException.class);

        Assertions.assertThrows(
                JwtParceClaimsException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }

    @Test
    public void refreshTokenNullTokenExceptionTest() {
        Mockito.when(jwtTokenUtils.getUserIdFromRefreshToken(Mockito.nullable(String.class)))
                .thenThrow(JwtParceClaimsException.class);

        Assertions.assertThrows(
                JwtParceClaimsException.class,
                () -> tokensService.refreshTokens(REFRESH_TOKEN_SAMPLE));
    }


}
