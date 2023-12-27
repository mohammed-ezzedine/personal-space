package me.ezzedine.mohammed.personalspace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ezzedine.mohammed.personalspace.article.infra.ArticleEntity;
import me.ezzedine.mohammed.personalspace.article.infra.ArticleMongoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataBackwardsCompatibilityEnabler implements CommandLineRunner {

    private final ArticleMongoRepository articleMongoRepository;

    @Override
    public void run(String... args) {
        List<ArticleEntity> articles = articleMongoRepository.findAll();
        for (ArticleEntity article : articles) {

            if (article.getEstimatedReadingTime() == null) {
                article.setEstimatedReadingTime("");
            }

            articleMongoRepository.save(article);
        }
    }
}
