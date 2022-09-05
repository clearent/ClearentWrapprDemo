package com.example.clearentwrapperdemo.feature

import androidx.lifecycle.ViewModel
import com.clearent.idtech.android.wrapper.SDKWrapper
import com.clearent.idtech.android.wrapper.http.model.SaleEntity
import com.clearent.idtech.android.wrapper.model.ReaderStatus
import com.example.clearentwrapperdemo.data.ClearentDemoDataSource

class MainViewModel : ViewModel() {

    val dataFlow = ClearentDemoDataSource.dataFlow

    fun searchReaders() = SDKWrapper.startSearching()
    fun stopSearching() = SDKWrapper.stopSearching()
    fun pairReader(readerStatus: ReaderStatus) = SDKWrapper.selectReader(readerStatus)

    fun startTransaction(amount: Double) =
        SDKWrapper.startTransactionWithAmount(
            SaleEntity(
                amount = SaleEntity.formatAmount(amount)
            )
        )
}