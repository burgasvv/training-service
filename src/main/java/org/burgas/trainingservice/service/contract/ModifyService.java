package org.burgas.trainingservice.service.contract;

import org.burgas.trainingservice.dto.Request;
import org.burgas.trainingservice.dto.Response;

public interface ModifyService<Req extends Request, Res extends Response> {

    Res update(Req request);
}
