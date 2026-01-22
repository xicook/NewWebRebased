package app.x1co.newwebclaude

data class Tab(
    val id: Int,
    var url: String,
    var title: String
)

class TabManager {
    private val tabs = mutableListOf<Tab>()
    private var currentTabId = 0
    private var nextId = 1

    init {
        addTab("https://www.google.com", "Google")
    }

    fun addTab(url: String, title: String): Tab {
        val tab = Tab(nextId++, url, title)
        tabs.add(tab)
        currentTabId = tab.id
        return tab
    }

    fun removeTab(id: Int) {
        tabs.removeIf { it.id == id }
        if (tabs.isEmpty()) {
            addTab("https://www.google.com", "Google")
        }
    }

    fun updateCurrentTab(url: String, title: String) {
        tabs.find { it.id == currentTabId }?.apply {
            this.url = url
            this.title = title
        }
    }

    fun getTabs(): List<Tab> = tabs.toList()

    fun getCurrentTab(): Tab? = tabs.find { it.id == currentTabId }
}