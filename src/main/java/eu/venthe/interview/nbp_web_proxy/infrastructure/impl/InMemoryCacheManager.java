package eu.venthe.interview.nbp_web_proxy.infrastructure.impl;

import eu.venthe.interview.nbp_web_proxy.shared_kernel.CacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCacheManager<T> implements CacheManager<T> {
   private final Map<String, T> cache = new HashMap<>();

    @Override
    public boolean isCached(String cacheKey) {
        return cache.containsKey(cacheKey);
    }

    @Override
    public void store(String cacheKey, T value) {
        cache.put(cacheKey, value);
    }

    @Override
    public Optional<T> retrieve(String cacheKey) {
        return Optional.ofNullable(cache.get(cacheKey));
    }
}
