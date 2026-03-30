package models.review;

import java.awt.*;

public record ReviewPostRequestBodyModel(int club, String review,
                                             int assessment, int readPages) {
}

