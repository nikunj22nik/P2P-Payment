package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.HistoryItem
import com.p2p.application.model.TransactionHistoryResponse
import com.p2p.application.model.TransactionItem
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

     var  list: MutableList<HistoryItem> = mutableListOf()
    var currentPage = 1
    var isLoading = false
    var isLastPage = false
    val limit = 10
    var mainList : MutableList<HistoryItem> = mutableListOf()

    suspend fun getTransactionHistory(): Flow<NetworkResult<TransactionHistoryResponse>> {
        isLoading = true
        return repository.getTransactionHistory(currentPage, limit).onEach {

        }
    }

    suspend fun genOneToOneTransactionHistory(
        @Field("user_id") userId :Int
    ) :Flow<NetworkResult<TransactionHistoryResponse>>{
        return repository.genOneToOneTransactionHistory(userId).onEach {  }
    }




    fun nextPage() {
        if (!isLastPage) {
            currentPage++
        }
    }
}