package api;

public class ApiClient {

    public final AuthApiClient auth = new AuthApiClient();
    public final UserApiClient user = new UserApiClient();
    public final ClubApiClient club = new ClubApiClient();
    public final ReviewApiClient review = new ReviewApiClient();
}
