package com.wu.euwallet.duplicatecheck.service;

import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;

public interface MarqetaUpdateService {

    void updateCard(ProfileUpdateRequest request, TransactionData transactionData);
}
