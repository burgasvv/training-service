package org.burgas.trainingservice.service.contract;

import org.burgas.trainingservice.dto.Response;

import java.util.Set;

public interface CollectService<R extends Response> {

    Set<R> findAll();
}
