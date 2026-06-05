package org.burgas.trainingservice.service.contract;

import jakarta.servlet.http.Part;
import org.burgas.trainingservice.dao.file.File;

public interface FileService<ID, F extends File> {

    F findEntity(ID id);

    F upload(Part part);

    void remove(F file);
}
