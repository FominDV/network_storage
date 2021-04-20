package ru.fomin.services;

import ru.fomin.dto.DataPackage;

public interface ResponseProcessor {

    void processResponse(DataPackage response);

}
