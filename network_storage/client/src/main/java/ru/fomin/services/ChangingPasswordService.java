package ru.fomin.services;

import ru.fomin.dto.requests.ChangePasswordRequest;

public interface ChangingPasswordService {

    void changePassword(String currentPassword, String newPassword);

}
