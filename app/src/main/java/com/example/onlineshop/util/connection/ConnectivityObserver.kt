package com.example.onlineshop.util.connection

import kotlinx.coroutines.flow.Flow


interface ConnectivityObserver {

    fun observe(): Flow<Status>

    enum class Status{
        Available, Unavailable, Lost,PositiveConnection,NegativeConnection
    }
}