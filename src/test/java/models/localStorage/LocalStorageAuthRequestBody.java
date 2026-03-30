package models.localStorage;

public record LocalStorageAuthRequestBody(UserData userData, String accessToken, String refreshToken,
                                          boolean isAuthenticated) {
}
