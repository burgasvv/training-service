package org.burgas.trainingservice.service.contract;

import org.burgas.trainingservice.dto.Request;
import org.burgas.trainingservice.dto.Response;

public interface DesignService<ID, Req extends Request, Res extends Response> {

    Res create(Req request);

    void delete(ID id);
}
