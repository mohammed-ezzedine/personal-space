package me.ezzedine.mohammed.personalspace.article.infra;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.article.core.ArticleSerialNumberProvider;
import me.ezzedine.mohammed.personalspace.util.sequence.SerialNumberSequence;
import me.ezzedine.mohammed.personalspace.util.sequence.SerialNumberSequenceMongoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSerialNumberMongoProvider implements ArticleSerialNumberProvider {

    static final String ARTICLE_SERIAL_NUMBER_ID = "article_serial_number";

    private final SerialNumberSequenceMongoRepository repository;

    @Override
    public long generateSequenceNumber() {
        SerialNumberSequence serialNumberSequence = repository.findById(ARTICLE_SERIAL_NUMBER_ID).orElse(getDefaultNumberSequence());
        long result = serialNumberSequence.getSequence();
        serialNumberSequence.setSequence(serialNumberSequence.getSequence() + 1);
        repository.save(serialNumberSequence);
        return result;
    }

    private static SerialNumberSequence getDefaultNumberSequence() {
        return SerialNumberSequence.builder().id(ARTICLE_SERIAL_NUMBER_ID).sequence(1L).build();
    }
}
