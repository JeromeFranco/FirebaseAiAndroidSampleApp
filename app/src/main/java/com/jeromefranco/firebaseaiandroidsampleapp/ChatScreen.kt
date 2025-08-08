package com.jeromefranco.firebaseaiandroidsampleapp

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jeromefranco.firebaseaiandroidsampleapp.ui.components.ChatInputBar
import com.jeromefranco.firebaseaiandroidsampleapp.ui.components.ChatMessageItem
import com.jeromefranco.firebaseaiandroidsampleapp.utils.toBitmap

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(), modifier: Modifier) {
    var userPrompt by remember { mutableStateOf("") }
    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageBitmap = uri?.toBitmap(context)
    }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.messages) {
        if (lazyListState.layoutInfo.totalItemsCount > 0) {
            val lastItemIndex = lazyListState.layoutInfo.totalItemsCount - 1
            lazyListState.animateScrollToItem(index = lastItemIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages) { message ->
                ChatMessageItem(message)
            }
        }

        selectedImageBitmap?.let {
            Row {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        val isSendEnabled = userPrompt.isNotBlank() || selectedImageBitmap != null

        ChatInputBar(
            prompt = userPrompt,
            onPromptChange = { userPrompt = it },
            onImagePickerClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onSendClick = {
                viewModel.sendChatMessage(
                    message = userPrompt,
                    imageAttachment = selectedImageBitmap
                )
                // Reset state after sending
                userPrompt = ""
                selectedImageBitmap = null
            },
            isSendEnabled = isSendEnabled
        )
    }
}
