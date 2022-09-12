package com.example.clearentwrapperdemo.feature

import androidx.lifecycle.ViewModel
import com.clearent.idtech.android.wrapper.ClearentWrapper
import com.clearent.idtech.android.wrapper.http.model.SaleEntity
import com.clearent.idtech.android.wrapper.model.ReaderStatus
import com.example.clearentwrapperdemo.data.ClearentDemoDataSource

class MainViewModel : ViewModel() {

    val dataFlow = ClearentDemoDataSource.dataFlow

    fun searchReaders() = ClearentWrapper.startSearching()
    fun stopSearching() = ClearentWrapper.stopSearching()
    fun pairReader(readerStatus: ReaderStatus) = ClearentWrapper.selectReader(readerStatus)

    fun startTransaction(amount: Double) =
        ClearentWrapper.startTransaction(
            SaleEntity(
                amount = SaleEntity.formatAmount(amount)
            )
        )
}