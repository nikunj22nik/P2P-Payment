package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SendMoneyViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {




}