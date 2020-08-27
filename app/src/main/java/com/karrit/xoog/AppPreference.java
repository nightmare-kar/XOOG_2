package com.karrit.xoog;

public class AppPreference {

    private boolean isOverrideResultScreen = true;

    public static int selectedTheme = -1;

    private boolean isDisableWallet, isDisableSavedCards, isDisableNetBanking, isDisableThirdPartyWallets, isDisableExitConfirmation;

    boolean isDisableWallet() {
        return isDisableWallet;
    }

    void setDisableWallet(boolean disableWallet) {
        isDisableWallet = disableWallet;
    }

    boolean isDisableSavedCards() {
        return isDisableSavedCards;
    }

    void setDisableSavedCards(boolean disableSavedCards) {
        isDisableSavedCards = disableSavedCards;
    }

    boolean isDisableNetBanking() {
        return isDisableNetBanking;
    }

    void setDisableNetBanking(boolean disableNetBanking) {
        isDisableNetBanking = disableNetBanking;
    }
    boolean isDisableThirdPartyWallets() {
        return isDisableThirdPartyWallets;
    }

    void setDisableThirdPartyWallets(boolean disableThirdPartyWallets) {
        isDisableThirdPartyWallets = disableThirdPartyWallets;
    }
    boolean isDisableExitConfirmation() {
        return isDisableExitConfirmation;
    }

    void setDisableExitConfirmation(boolean disableExitConfirmation) {
        isDisableExitConfirmation = disableExitConfirmation;
    }

}

