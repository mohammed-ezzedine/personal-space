package me.ezzedine.mohammed.personalspace.article.api.image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadImageApiResponse {
    private String url;
}
