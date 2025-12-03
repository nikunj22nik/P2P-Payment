package com.p2p.application.model

sealed class DialogAction {
    object BackToHome : DialogAction()
    object BackToLogin : DialogAction()
    object TryAgain : DialogAction()
    object ContinueRegistration : DialogAction()
}