package com.karan.schemesaathi.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.schemesaathi.data.model.Category
import com.karan.schemesaathi.ui.components.AppHeader
import com.karan.schemesaathi.ui.components.LanguageSelectorDialog
import com.karan.schemesaathi.ui.components.OpportunityCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpportunityClick: (String) -> Unit,
    onExploreCategoryClick: (Category) -> Unit,
    onAskSaathiClick: () -> Unit,
    onAlertsClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showLanguageModal by remember { mutableStateOf(false) }

    if (showLanguageModal) {
        LanguageSelectorDialog(
            currentLanguageCode = state.selectedLanguage,
            onLanguageSelected = { /* Updated via profile */ },
            onDismiss = { showLanguageModal = false }
        )
    }

    Scaffold(
        topBar = {
            AppHeader(
                userName = state.userProfile?.fullName,
                selectedLanguage = state.selectedLanguage,
                onLanguageClick = { showLanguageModal = true },
                searchQuery = state.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // ASK SAATHI AI ENTRY BANNER
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAskSaathiClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ask Saathi AI Assistant",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Ask questions about eligibility, documents, or steps in English, Hindi & regional languages.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // QUICK CATEGORIES GRID
                Text(
                    text = "Quick Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Category.SCHOLARSHIPS,
                        Category.JOBS,
                        Category.INTERNSHIPS,
                        Category.FARMING,
                        Category.PENSIONS,
                        Category.HEALTH_INSURANCE,
                        Category.HOUSING,
                        Category.SELF_EMPLOYMENT
                    ).forEach { cat ->
                        Surface(
                            onClick = { onExploreCategoryClick(cat) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BEST MATCHES SECTION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Best Matches for You",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(state.bestMatches) { (opp, status) ->
                OpportunityCard(
                    opportunity = opp,
                    eligibilityStatus = status,
                    isSaved = state.savedItemIds.contains(opp.id),
                    onSaveClick = { viewModel.toggleSaveOpportunity(opp.id) },
                    onClick = { onOpportunityClick(opp.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // ALL NEW OPPORTUNITIES
                Text(
                    text = "New Schemes & Jobs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(state.newOpportunities) { opp ->
                OpportunityCard(
                    opportunity = opp,
                    eligibilityStatus = null,
                    isSaved = state.savedItemIds.contains(opp.id),
                    onSaveClick = { viewModel.toggleSaveOpportunity(opp.id) },
                    onClick = { onOpportunityClick(opp.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
