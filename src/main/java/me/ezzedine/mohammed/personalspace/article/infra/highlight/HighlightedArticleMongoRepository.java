package me.ezzedine.mohammed.personalspace.article.infra.highlight;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HighlightedArticleMongoRepository extends MongoRepository<HighlightedArticleEntity, String> {
}
