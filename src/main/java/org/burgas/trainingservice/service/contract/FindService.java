package org.burgas.trainingservice.service.contract;

import org.burgas.trainingservice.dao.Dao;
import org.burgas.trainingservice.dto.Response;

public interface FindService<ID, E extends Dao, R extends Response> {

    E findEntity(ID id);

    R findById(ID id);
}
