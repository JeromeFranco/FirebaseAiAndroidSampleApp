package com.jeromefranco.firebaseaiandroidsampleapp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.jeromefranco.firebaseaiandroidsampleapp.utils.Location
import com.jeromefranco.firebaseaiandroidsampleapp.utils.fetchWeather
import com.jeromefranco.firebaseaiandroidsampleapp.utils.fetchWeatherTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

enum class ContentRole(val value: String) {
    USER("user"), MODEL("model")
}

data class ChatMessage(
    val role: ContentRole = ContentRole.USER,
    val content: String? = null,
    val imageAttachment: Bitmap? = null,
    val hint: String? = null
)

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isStreaming: Boolean = false,
)


class ChatViewModel : ViewModel() {
    private val chat = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-2.5-flash",
            tools = listOf(Tool.functionDeclarations(listOf(fetchWeatherTool)))
        ).startChat(history = mutableListOf())


    private var _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun sendChatMessage(message: String, imageAttachment: Bitmap? = null) {
        val userPrompt = content(role = ContentRole.USER.value) {
            imageAttachment?.let { image(it) }
            text(message)
        }
        val messages = _state.value.messages +
                ChatMessage(
                    role = ContentRole.USER,
                    content = message,
                    imageAttachment = imageAttachment
                ) +
                ChatMessage(
                    role = ContentRole.MODEL,
                    content = "",
                    hint = "Thinking..."
                )

        _state.update {
            it.copy(
                isStreaming = true,
                messages = messages
            )
        }

        viewModelScope.launch {
            sendMessage(userPrompt)
        }
    }

    private suspend fun sendMessage(prompt: Content) {
        var functionCalls: List<FunctionCallPart> = emptyList()

        chat.sendMessageStream(prompt)
            .onCompletion { cause ->
                if (functionCalls.isNotEmpty()) {
                    handleFunctionCalls(functionCalls)
                    return@onCompletion
                }

                _state.update {
                    val messages = it.messages.toMutableList()
                    if (cause != null) {
                        messages[messages.lastIndex] = messages.last().copy(
                            content = "Error: ${cause.message}"
                        )
                    }
                    it.copy(isStreaming = false, messages = messages)
                }
            }
            .collect { chunk ->
                if (chunk.functionCalls.isNotEmpty()) {
                    functionCalls = chunk.functionCalls
                    return@collect
                }

                chunk.text?.let { text ->
                    _state.update {
                        val messages = it.messages.toMutableList()
                        messages[messages.lastIndex] = messages.last().copy(
                            content = messages.last().content + text,
                            hint = null
                        )
                        it.copy(messages = messages)
                    }
                }
            }
    }

    private suspend fun handleFunctionCalls(functionCalls: List<FunctionCallPart>) {
        functionCalls.forEach { call ->
            when (call.name) {
                "fetchWeather" -> {
                    val location = Location(
                        city = call.args["location"]!!.jsonObject["city"]!!.jsonPrimitive.content,
                        state = call.args["location"]!!.jsonObject["state"]!!.jsonPrimitive.content
                    )
                    val date = call.args["date"]!!.jsonPrimitive.content

                    val result = fetchWeather(location, date)

                    val functionResponsePrompt = content("function") {
                        part(FunctionResponsePart("fetchWeather", result))
                    }
                    _state.update {
                        val messages = it.messages.toMutableList()
                        messages[messages.lastIndex] = messages.last().copy(
                            hint = "Tool: Fetching weather..."
                        )
                        it.copy(messages = messages)
                    }
                    sendMessage(functionResponsePrompt)
                }

                else -> {} // Do nothing for now
            }
        }
    }
}
