package models.review;

public record ReviewPostRequestBodyModel(int club, String review,
                                         int assessment, int readPages) {
}

