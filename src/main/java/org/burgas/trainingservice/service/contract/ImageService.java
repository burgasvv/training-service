package org.burgas.trainingservice.service.contract;

import jakarta.servlet.http.Part;
import org.burgas.trainingservice.dao.image.Image;

public interface ImageService<ID, I extends Image> {

    I findEntity(ID id);

    I upload(Part part);

    void remove(ID id);
}
