package com.example.readium.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class TradeStatus {
    PENDING,   // Pendente
    ACCEPTED,  // Aceita
    REJECTED,  // Rejeitada
    COMPLETED  // Concluída
}

data class TradeProposal(
    var id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val desiredBookId: String = "",
    val desiredBookTitle: String = "",
    val offeredBookId: String? = null,
    val offeredBookTitle: String? = null,
    val status: TradeStatus = TradeStatus.PENDING,

    @ServerTimestamp
    val createdAt: Date? = null
)