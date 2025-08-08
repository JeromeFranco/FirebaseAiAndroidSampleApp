package com.jeromefranco.firebaseaiandroidsampleapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.jeromefranco.firebaseaiandroidsampleapp.ChatMessage
import com.jeromefranco.firebaseaiandroidsampleapp.ContentRole


@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUserMessage = message.role == ContentRole.USER
    val alignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isUserMessage) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isUserMessage) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSecondaryContainer


    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .align(alignment)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUserMessage) 16.dp else 0.dp,
                        bottomEnd = if (isUserMessage) 0.dp else 16.dp
                    )
                )
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 300.dp)
        ) {
            message.imageAttachment?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp)
                )
            }
            Row {
                if (!message.hint.isNullOrEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp),
                        color = textColor,
                        strokeWidth = 2.dp

                    )
                }
                val text = message.content
                    .takeIf { it != null && it.isNotBlank() }
                    ?: message.hint
                    ?: ""

                Text(text = text, color = textColor)
            }
        }
    }
}
