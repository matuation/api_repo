package models.review;

import java.util.List;
import java.util.Map;

public record ReviewPostResponseBodyModel(int id, int club, Map<String, Object> user, String review,
                                          int assessment, int readPages, String created,
                                          String modified) {
}
