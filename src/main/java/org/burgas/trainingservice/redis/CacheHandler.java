package org.burgas.trainingservice.redis;

import org.burgas.trainingservice.dao.Dao;

public interface CacheHandler<E extends Dao> {

    void handleCache(E entity);
}
