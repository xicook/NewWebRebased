package app.x1co.newwebclaude

import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

class HistoryManager {
    private val history = mutableListOf<HistoryItem>()
    private val maxHistorySize = 100

    fun addToHistory(url: String, title: String) {
        // Não adiciona páginas bloqueadas ou vazias
        if (url.isBlank() || WebShield.isBlocked(url)) {
            return
        }

        val item = HistoryItem(url, title)

        // Remove duplicatas recentes
        history.removeIf { it.url == url }

        // Adiciona no início
        history.add(0, item)

        // Limita o tamanho
        if (history.size > maxHistorySize) {
            history.removeAt(history.lastIndex)
        }
    }

    fun getHistory(): List<HistoryItem> = history.toList()

    fun clearHistory() {
        history.clear()
    }

    fun removeItem(url: String) {
        history.removeIf { it.url == url }
    }
}