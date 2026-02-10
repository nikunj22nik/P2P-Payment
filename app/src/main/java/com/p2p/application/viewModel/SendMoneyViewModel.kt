package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Receiver
import com.p2p.application.model.ReceiverInfo
import com.p2p.application.model.Transaction
import com.p2p.application.model.accountlimit.AccountLimitModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import javax.inject.Inject


@HiltViewModel
class SendMoneyViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {


    var receiver : Receiver? = null
    suspend fun accountLimitRequest(): Flow<NetworkResult<AccountLimitModel>>{
        return repository.accountLimitRequest()
    }
    suspend fun sendMoney(
        senderType: String,
        receiver_id: Int,
        receiverType: String,
        amount: String,
        confirmAmount: String,
        currentTime: String,
        currentDate: String
    ): Flow<NetworkResult<Transaction>>{
        return repository.sendMoney(senderType,receiver_id,receiverType,amount,confirmAmount,
            currentTime,currentDate
            ).onEach {

        }
    }

    suspend fun getBalance(): Flow<NetworkResult<Pair<String,String>>>{
        return repository.getBalance().onEach {

        }
    }


    suspend fun receiverProfileImage(receiverId: Int): Flow<NetworkResult<ReceiverInfo>>{
        return repository.receiverProfileImage(receiverId).onEach {

        }
    }
    suspend fun checkSecretCode(secretCode: String): Flow<NetworkResult<Boolean>>{
        return repository.checkSecretCode(secretCode).onEach {

        }
    }


    suspend fun withDraw(
        @Field("sender_id") senderId :String,
        @Field("sender_type") senderType :String,
        @Field("amount") amount :String,
        @Field("time") time :String,
        @Field("date") date :String
    ) : Flow<NetworkResult<String>>{
        return repository.withDraw(
            senderId,senderType,amount,time,date
        ).onEach {

        }
    }

}