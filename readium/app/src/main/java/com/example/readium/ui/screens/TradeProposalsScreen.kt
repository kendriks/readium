package com.example.readium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readium.data.model.TradeProposal
import com.example.readium.data.model.TradeStatus
import com.example.readium.ui.theme.*
import com.example.readium.viewmodel.TradeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TradeProposalsScreen(
    viewModel: TradeViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(currentUserId) {
        currentUserId?.let { viewModel.loadReceivedProposals(it) }
    }

    Scaffold(
        topBar = {
            ProposalsTopBar(onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ReadiumBackground)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ReadiumPrimary)
                }
            } else if (viewModel.receivedProposals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma proposta recebida.", color = ReadiumGrayMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.receivedProposals) { proposal ->
                        ProposalCard(
                            proposal = proposal,
                            onAccept = { viewModel.respondToProposal(proposal, true) },
                            onReject = { viewModel.respondToProposal(proposal, false) }


                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProposalCard(
    proposal: TradeProposal,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ReadiumWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Livro desejado: ${proposal.desiredBookTitle}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ReadiumBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Solicitante: ${proposal.senderName}",
                fontSize = 14.sp,
                color = ReadiumBlack.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Status ou Botões de Ação
            if (proposal.status == TradeStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReadiumError)
                    ) {
                        Text("Recusar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = ReadiumPrimary)
                    ) {
                        Text("Aceitar", color = ReadiumWhite)
                    }
                }
            } else {
                // Exibe o status final
                val statusText = when (proposal.status) {
                    TradeStatus.ACCEPTED -> "ACEITA"
                    TradeStatus.REJECTED -> "REJEITADA"
                    else -> ""
                }
                val statusColor = if (proposal.status == TradeStatus.ACCEPTED)
                    Color(0xFF4CAF50) else ReadiumError

                Text(
                    text = "Status: $statusText",
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ProposalsTopBar(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 34.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(76.dp),
            color = ReadiumPrimary,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = ReadiumWhite)
                }
                Text(
                    "Propostas Recebidas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReadiumWhite
                )
            }
        }
    }
}