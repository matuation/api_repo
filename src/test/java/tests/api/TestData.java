package tests.api;

import net.datafaker.Faker;

public class TestData {
    public static final Faker faker = new Faker();
    public static final String USERNAME = "user8";
    public static final String PASSWORD = "user8";
    public static final String WRONG_USERNAME = "qaruru";
    public static final String WRONG_PASSWORD = "qaruru123";
    public static final String EMPTY_STRING = "";
    public static final String NULL_STRING = null;
    public static final double WRONG_FORMAT = 0.0;
    public static final String EXPECTED_TOKEN_PATH = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String BLANK_FIELD_ERROR = "This field may not be blank.";
    public static final String NULL_FIELD_ERROR = "This field may not be null.";
    public static final String REQUIRED_FIELD_ERROR = "This field is required.";
    public static final String EXISTING_USER_ERROR = "A user with that username already exists.";
    public static final String INVALID_USERNAME_ERROR = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
    public static final String INVALID_EMAIL_ERROR = "Enter a valid email address.";
    public static final String EXCEEDED_USERNAME_ERROR = "Ensure this field has no more than 150 characters.";
    public static final String EXCEEDED_PASSWORD_ERROR = "Ensure this field has no more than 128 characters.";
    public static final String EXCEEDED_NAME_ERROR = "Ensure this field has no more than 150 characters.";
    public static final String EXCEEDED_EMAIL_ERROR = "Ensure this field has no more than 254 characters.";
    public static final String BAD_TOKEN = "1";
    public static final String IP_ADR_REGEXP = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
            + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
    public static final String TELEGRAM_LINK = "https://t.me/+D0Dyz7lRd7djODQy";
    public static final String NEW_TELEGRAM_LINK = "https://t.me/+N3Wyz7lRd7djONEW";
    public static final String NO_CLUB_ERROR = "No Club matches the given query.";
    public static final String NO_REVIEW_ERROR = "No BookReview matches the given query.";
    public static final String NO_PERMISSION_ERROR = "You do not have permission to perform this action.";
    public static final String ALREADY_SIGNED_ERROR = "User is already a member of this club.";
}
