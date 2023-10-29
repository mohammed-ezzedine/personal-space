package me.ezzedine.mohammed.personalspace.article.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleMongoRepository extends MongoRepository<ArticleEntity, String> {
}
