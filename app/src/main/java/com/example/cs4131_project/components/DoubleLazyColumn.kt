package com.example.cs4131_project.components

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DoubleLazyColumn {
    companion object {
        @Composable
        fun <T> DoubleLazyColumn(
            items: ArrayList<T>,
            modifier: Modifier = Modifier,
            state: LazyListState = rememberLazyListState(),
            contentPadding: PaddingValues = PaddingValues(0.dp),
            reverseLayout: Boolean = false,
            horizontalAlignment: Alignment.Horizontal = Alignment.Start,
            flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
            userScrollEnabled: Boolean = true,
            onClick: (item: T) -> Unit,
            content: @Composable (item: T) -> Unit
        ) {
            LazyColumn(
                state = state,
                modifier = modifier,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = horizontalAlignment,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled
            ) {
                items((items.size + 1) / 2) { index ->
                    DoubleCard(items[index * 2], if (index * 2 + 1 < items.size) items[index * 2 + 1] else null, onClick, content)
                }
            }
        }

        @Composable
        private fun <T> DoubleCard(
            item1: T,
            item2: T?,
            onClick: (item: T) -> Unit,
            content: @Composable (item: T) -> Unit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onClick = { onClick(item1) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        content(item1)
                    }
                }
                if (item2 != null) {
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        onClick = { onClick(item2 ) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            content(item2)
                        }
                    }
                }
                else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                }
            }
        }
    }
}