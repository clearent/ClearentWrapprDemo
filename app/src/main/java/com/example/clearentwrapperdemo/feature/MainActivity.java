package com.example.clearentwrapperdemo.feature;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.clearent.idtech.android.wrapper.ClearentWrapper;
import com.clearent.idtech.android.wrapper.http.model.ResponseError;
import com.clearent.idtech.android.wrapper.http.model.SignatureResponse;
import com.clearent.idtech.android.wrapper.http.model.TransactionResponse;
import com.clearent.idtech.android.wrapper.http.model.TransactionResult;
import com.clearent.idtech.android.wrapper.listener.ClearentWrapperListener;
import com.clearent.idtech.android.wrapper.model.ReaderStatus;
import com.clearent.idtech.android.wrapper.model.UserAction;
import com.clearent.idtech.android.wrapper.model.UserInfo;
import com.clearent.idtech.android.wrapper.ui.util.PermissionsKt;
import com.example.clearentwrapperdemo.R;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements ClearentWrapperListener {

    private static final String generalErrorMessage = "General error encountered";
    private static final String transactionLoadingStatusTitle = "Transaction status";
    private static final String pairingLoadingStatusTitle = "Pairing status";

    private MainViewModel viewModel;

    private final ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract =
            new ActivityResultContracts.RequestMultiplePermissions();
    private final ActivityResultLauncher<String[]> multiplePermissionsLauncher =
            registerForActivityResult(multiplePermissionsContract, result -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);

        setupSdkListener();
        askPermissions();

        navigateToHomeScreen();
    }

    private void askPermissions() {
        multiplePermissionsLauncher.launch(PermissionsKt.checkPermissionsToRequest(getApplicationContext()));
    }

    private void setupSdkListener() {
        ClearentWrapper.INSTANCE.setListener(this);
    }

    private void navigateToScreen(Fragment screen) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, screen, null)
                .commit();
    }

    private void navigateToHomeScreen() {
        navigateToScreen(
                new HomeFragment(
                        () -> {
                            viewModel.searchReaders();
                            navigateToPairingScreen(Collections.emptyList());
                        },
                        viewModel::startTransaction
                )
        );
    }

    private void navigateToLoadingFragment(String title, String message) {
        navigateToScreen(
                new LoadingFragment(
                        title,
                        message
                )
        );
    }

    private void navigateToResultScreen(ResultFragment.ResultMessage resultMessage) {
        navigateToScreen(
                new ResultFragment(
                        resultMessage,
                        (Runnable) this::navigateToHomeScreen
                )
        );
    }

    private void navigateToPairingScreen(@NonNull List<ReaderStatus> list) {
        navigateToScreen(
                new PairingFragment(
                        list,
                        (Consumer<ReaderStatus>) readerStatus -> {
                            viewModel.pairReader(readerStatus);
                            navigateToLoadingFragment(pairingLoadingStatusTitle, "Pairing ongoing");
                        },
                        () -> {
                            viewModel.stopSearching();
                            navigateToHomeScreen();
                        }

                )
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClearentWrapper.INSTANCE.removeListener();
    }

    @Override
    public void deviceDidConnect() {
        navigateToResultScreen(
                new ResultFragment.ResultMessage(
                        "Device connected.",
                        null,
                        ResultFragment.ResultType.SUCCESS
                )
        );
    }

    @Override
    public void deviceDidDisconnect() {
        navigateToResultScreen(
                new ResultFragment.ResultMessage(
                        "Device disconnected.",
                        null,
                        ResultFragment.ResultType.FAILURE
                )
        );
    }

    @Override
    public void didEncounteredGeneralError() {
        navigateToResultScreen(
                new ResultFragment.ResultMessage(
                        generalErrorMessage,
                        null,
                        ResultFragment.ResultType.FAILURE
                )
        );
    }

    @Override
    public void didFindReaders(@NonNull List<ReaderStatus> list) {
        navigateToPairingScreen(list);
    }

    @Override
    public void didFinishSignature(@Nullable SignatureResponse signatureResponse, @Nullable ResponseError responseError) {
        if (signatureResponse != null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            generalErrorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }

        String errorMessage = signatureResponse.getPayload().getError().getErrorMessage();
        if (errorMessage != null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            errorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }
        errorMessage = responseError.getErrorMessage();
        if (errorMessage != null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            errorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }
        navigateToResultScreen(
                new ResultFragment.ResultMessage(
                        "Signature success.",
                        null,
                        ResultFragment.ResultType.SUCCESS
                )
        );
    }

    @Override
    public void didFinishTransaction(@Nullable TransactionResponse transactionResponse, @Nullable ResponseError responseError) {
        if (transactionResponse == null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            generalErrorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }

        if (transactionResponse.getPayload().getTransaction().getResult().equals(TransactionResult.APPROVED.name())) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            "Transaction success.",
                            null,
                            ResultFragment.ResultType.SUCCESS
                    )
            );
            return;
        }
        String errorMessage = transactionResponse.getPayload().getTransaction().getDisplayMessage();
        if (errorMessage != null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            errorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }
        errorMessage = responseError.getErrorMessage();
        if (errorMessage != null) {
            navigateToResultScreen(
                    new ResultFragment.ResultMessage(
                            errorMessage,
                            null,
                            ResultFragment.ResultType.FAILURE
                    )
            );
            return;
        }
        navigateToResultScreen(
                new ResultFragment.ResultMessage(
                        generalErrorMessage,
                        null,
                        ResultFragment.ResultType.FAILURE
                )
        );
    }

    @Override
    public void didReceiveInfo(@NonNull UserInfo userInfo) {
        navigateToLoadingFragment(transactionLoadingStatusTitle, userInfo.getMessage());
    }

    @Override
    public void didStartBTSearch() {
    }

    @Override
    public void didStartReaderConnection(@NonNull ReaderStatus readerStatus) {
    }

    @Override
    public void userActionNeeded(@NonNull UserAction userAction) {
        navigateToLoadingFragment(transactionLoadingStatusTitle, userAction.getMessage());
    }
}