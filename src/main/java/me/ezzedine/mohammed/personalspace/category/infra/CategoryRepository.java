package me.ezzedine.mohammed.personalspace.category.infra;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findFirstByOrderByOrderDesc();
}
