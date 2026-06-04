package org.burgas.trainingservice.mapper;

import org.burgas.trainingservice.dao.Dao;
import org.burgas.trainingservice.dto.Dependency;
import org.burgas.trainingservice.dto.Request;
import org.burgas.trainingservice.dto.Response;

public interface Mapper<Req extends Request, Ent extends Dao, Dep extends Dependency, Res extends Response> {

    Ent toEntity(Req request);

    Dep toDependency(Ent entity);

    Res toResponse(Ent entity);

    default <D> D handleData(D requestData, D entityData) {
        return requestData == null ? entityData : requestData;
    }

    default <D> D handleDataException(D requestData, String message) {
        if (requestData == null) throw new IllegalArgumentException(message);
        return requestData;
    }
}
