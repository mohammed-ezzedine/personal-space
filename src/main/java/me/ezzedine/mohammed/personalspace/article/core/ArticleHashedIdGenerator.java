package me.ezzedine.mohammed.personalspace.article.core;

import lombok.RequiredArgsConstructor;
import me.ezzedine.mohammed.personalspace.util.hash.HashingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleHashedIdGenerator implements ArticleIdGenerator {

    private final ArticleSerialNumberProvider serialNumberProvider;
    private final HashingService hashingService;

    @Override
    public String generate() {
        long sequenceNumber = serialNumberProvider.generateSequenceNumber();
        return hashingService.hash(sequenceNumber);
    }
}
