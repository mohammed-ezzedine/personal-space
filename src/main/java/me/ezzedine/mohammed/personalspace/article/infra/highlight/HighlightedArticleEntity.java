package me.ezzedine.mohammed.personalspace.article.infra.highlight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class HighlightedArticleEntity {
    @Id
    private String articleId;
    private int rank;
}
