package models.localStorage;

public record UserData(
        int id,
        String username,
        String firstName,
        String lastName,
        String email,
        String remoteAddr) {
}
