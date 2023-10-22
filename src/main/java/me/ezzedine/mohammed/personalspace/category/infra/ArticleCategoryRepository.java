package me.ezzedine.mohammed.personalspace.category.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleCategoryRepository extends MongoRepository<ArticleCategoryEntity, String> {
}
