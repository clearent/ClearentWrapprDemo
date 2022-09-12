package com.example.clearentwrapperdemo.data

import com.clearent.idtech.android.wrapper.http.model.ResponseError
import com.clearent.idtech.android.wrapper.http.model.SignatureResponse
import com.clearent.idtech.android.wrapper.http.model.TransactionResponse
import com.clearent.idtech.android.wrapper.http.model.TransactionResult
import com.clearent.idtech.android.wrapper.listener.ClearentWrapperListener
import com.clearent.idtech.android.wrapper.model.ReaderStatus
import com.clearent.idtech.android.wrapper.model.UserAction
import com.clearent.idtech.android.wrapper.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object ClearentDemoDataSource : ClearentWrapperListener {

    private const val generalErrorMessage = "General error encountered"
    private const val transactionLoadingStatusTitle = "Transaction status"

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _dataFlow = MutableSharedFlow<DataStatus>()
    val dataFlow: SharedFlow<DataStatus> = _dataFlow

    override fun deviceDidConnect() {
        coroutineScope.launch {
            _dataFlow.emit(DataStatus.ResultMessage.Success("Device connected."))
        }
    }

    override fun deviceDidDisconnect() {
        coroutineScope.launch {
            _dataFlow.emit(DataStatus.ResultMessage.Error("Device disconnected."))
        }
    }

    override fun didEncounteredGeneralError() {
        coroutineScope.launch {
            _dataFlow.emit(DataStatus.ResultMessage.Error(generalErrorMessage))
        }
    }

    override fun didFindReaders(readers: List<ReaderStatus>) {
        coroutineScope.launch {
            _dataFlow.emit(DataStatus.ReadersList(readers))
        }
    }

    override fun didFinishSignature(response: SignatureResponse?, error: ResponseError?) {
        coroutineScope.launch {
            val errorMessage = response?.payload?.error?.errorMessage
                ?: error?.errorMessage

            errorMessage?.also {
                _dataFlow.emit(DataStatus.ResultMessage.Error(errorMessage))
            } ?: run {
                _dataFlow.emit(DataStatus.ResultMessage.Success("Signature success."))
            }
        }
    }

    override fun didFinishTransaction(response: TransactionResponse?, error: ResponseError?) {
        coroutineScope.launch {
            if (response?.payload?.transaction?.result == TransactionResult.APPROVED.name) {
                _dataFlow.emit(DataStatus.ResultMessage.Success("Transaction success."))
            } else {
                val transaction = response?.payload?.transaction
                val errorMessage = transaction?.displayMessage
                    ?: response?.payload?.error?.errorMessage
                    ?: error?.errorMessage
                    ?: generalErrorMessage
                _dataFlow.emit(DataStatus.ResultMessage.Error(errorMessage))
            }
        }
    }

    override fun didReceiveInfo(userInfo: UserInfo) {
        coroutineScope.launch {
            _dataFlow.emit(
                DataStatus.LoadingStatus(
                    transactionLoadingStatusTitle,
                    userInfo.message
                )
            )
        }
    }

    override fun didStartBTSearch() {}

    override fun didStartReaderConnection(reader: ReaderStatus) {}

    override fun userActionNeeded(userAction: UserAction) {
        coroutineScope.launch {
            _dataFlow.emit(
                DataStatus.LoadingStatus(
                    transactionLoadingStatusTitle,
                    userAction.message
                )
            )
        }
    }
}