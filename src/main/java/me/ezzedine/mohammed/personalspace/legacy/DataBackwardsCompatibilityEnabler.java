package me.ezzedine.mohammed.personalspace.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.infra.ArticleEntity;
import me.ezzedine.mohammed.personalspace.article.infra.ArticleMongoRepository;
import me.ezzedine.mohammed.personalspace.article.infra.highlight.HighlightedArticleMongoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataBackwardsCompatibilityEnabler implements CommandLineRunner {

    private final ArticleMongoRepository articleMongoRepository;
    private final HighlightedArticleMongoRepository highlightedArticleMongoRepository;

    @Override
    public void run(String... args) throws IOException {
        highlightedArticleMongoRepository.deleteAll();
        articleMongoRepository.deleteAll();

        byte[] bytes = DataBackwardsCompatibilityEnabler.class.getClassLoader().getResourceAsStream("migrated-data.json").readAllBytes();
        LegacyArticleDocument[] legacyDocuments = new ObjectMapper().readValue(bytes, LegacyArticleDocument[].class);

        List<ArticleEntity> entities = Arrays.stream(legacyDocuments).map(DataBackwardsCompatibilityEnabler::map).toList();

        articleMongoRepository.saveAll(entities);
    }

    private static ArticleEntity map(LegacyArticleDocument e) {
        return ArticleEntity.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .categoryId(e.getCategoryId())
                .thumbnailImageUrl(e.getThumbnailImageUrl())
                .createdDate(getDate(e.getCreatedDate()))
                .lastModifiedDate(getDate(e.getLastModifiedDate()))
                .estimatedReadingTime(e.getEstimatedReadingTime())
                .content(e.getContent())
                .build();
    }

    private static LocalDateTime getDate(String date) {
        return LocalDateTime.parse(date);
    }
}
