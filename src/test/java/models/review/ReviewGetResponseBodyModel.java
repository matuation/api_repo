package models.review;

import java.util.Map;

public record ReviewGetResponseBodyModel(int id, int club, Map<String, Object> user, String review,
                                         int assessment, int readPages, String created,
                                         String modified) {
}
