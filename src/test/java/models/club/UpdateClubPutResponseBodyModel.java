package models.club;

import java.util.List;

public record UpdateClubPutResponseBodyModel(String id, String bookTitle, String bookAuthors,
                                             int publicationYear, String description, String telegramChatLink,
                                             int owner, List<Integer> members, List<Integer> reviews,
                                             String created, String modified) {
}
