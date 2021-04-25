package ru.fomin.service;

import ru.fomin.dto.DataPackage;

public interface ResponseProcessor {

    void processResponse(DataPackage response);

}
