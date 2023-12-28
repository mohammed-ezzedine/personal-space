package me.ezzedine.mohammed.personalspace.util.hash;

import org.hashids.Hashids;
import org.springframework.stereotype.Service;

@Service
public class HashidHashingService implements HashingService {

    private final Hashids hashids;

    public HashidHashingService(HashingConfig config) {
        hashids = new Hashids(config.getSalt(), config.getSize());
    }

    @Override
    public String hash(long number) {
        return hashids.encode(number);
    }
}
