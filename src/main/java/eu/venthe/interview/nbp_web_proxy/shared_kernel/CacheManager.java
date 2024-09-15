package eu.venthe.interview.nbp_web_proxy.shared_kernel;

import java.util.Optional;

// TODO: Add evict cache
public interface CacheManager<T> {
    boolean isCached(String cacheKey);

    void store(String cacheKey, T value);

    Optional<T> retrieve(String cacheKey);
}
