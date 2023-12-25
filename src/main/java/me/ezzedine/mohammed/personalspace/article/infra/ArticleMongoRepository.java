package me.ezzedine.mohammed.personalspace.article.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleMongoRepository extends MongoRepository<ArticleEntity, String> {
    List<ArticleEntity> findByCategoryId(String categoryId);
}
