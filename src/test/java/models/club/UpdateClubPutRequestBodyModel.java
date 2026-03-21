package models.club;

public record UpdateClubPutRequestBodyModel(String bookTitle, String bookAuthors, int publicationYear,
                                            String description, String telegramChatLink) {
}
