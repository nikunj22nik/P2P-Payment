package com.p2p.application.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.HistoryItem
import com.p2p.application.model.TransactionHistoryResponse
import com.p2p.application.model.TransactionItem
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    var list : MutableList<HistoryItem> = mutableListOf()
    var currentPage = 1
    var isLoading = false
    var isLastPage = false
    val limit = 20
    var mainList : MutableList<HistoryItem> = mutableListOf()
    var seachedList :MutableList<HistoryItem> = mutableListOf()
    var isSearching :Boolean = false
    var filter :Boolean = false

    suspend fun getTransactionHistory(searchTxt :String=""): Flow<NetworkResult<TransactionHistoryResponse>> {
        isLoading = true
        Log.d("TESTING_DATA"," "+searchTxt)
        return if(searchTxt.isEmpty()) {
            repository.getTransactionHistory(currentPage, limit, searchTxt).onEach { }
        }
        else {
            repository.getTransactionHistory(1, 1, searchTxt).onEach { }
        }

    }

    suspend fun genOneToOneTransactionHistory(
        @Field("user_id") userId :Int,transactionType :String
    ) : Flow<NetworkResult<TransactionHistoryResponse>>{
        return repository.genOneToOneTransactionHistory(userId,transactionType).onEach {  }
    }

    fun nextPage() {
        if (!isLastPage && !isSearching) {
            currentPage++
        }
    }

     suspend fun getUserReceivedTransaction(): Flow<NetworkResult<TransactionHistoryResponse>>{
         return repository.getUserReceivedTransaction().onEach {
         }
     }

}