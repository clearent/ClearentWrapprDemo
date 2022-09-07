package com.example.clearentwrapperdemo.feature;

import androidx.lifecycle.ViewModel;

import com.clearent.idtech.android.wrapper.ClearentWrapper;
import com.clearent.idtech.android.wrapper.http.model.SaleEntity;
import com.clearent.idtech.android.wrapper.model.ReaderStatus;

public class MainViewModel extends ViewModel {

    public void searchReaders() {
        ClearentWrapper.INSTANCE.startSearching(null);
    }

    public void stopSearching() {
        ClearentWrapper.INSTANCE.stopSearching();
    }

    public void pairReader(ReaderStatus readerStatus) {
        ClearentWrapper.INSTANCE.selectReader(readerStatus, true);
    }

    public void startTransaction(Double amount) {
        if (amount == null)
            return;

        SaleEntity saleEntity = new SaleEntity(
                SaleEntity.Companion.formatAmount(amount),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        ClearentWrapper.INSTANCE.startTransaction(saleEntity, null);
    }
}
