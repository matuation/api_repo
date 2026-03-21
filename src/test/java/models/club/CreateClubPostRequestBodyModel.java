package models.club;

public record CreateClubPostRequestBodyModel(String bookTitle, String bookAuthors, int publicationYear,
                                             String description, String telegramChatLink) {
}
