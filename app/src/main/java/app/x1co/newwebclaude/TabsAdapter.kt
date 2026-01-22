package app.x1co.newwebclaude

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.x1co.newwebclaude.databinding.ItemTabBinding

class TabsAdapter(
    private val tabs: List<Tab>,
    private val onTabClick: (Tab) -> Unit
) : RecyclerView.Adapter<TabsAdapter.TabViewHolder>() {

    inner class TabViewHolder(private val binding: ItemTabBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tab: Tab) {
            binding.tabTitle.text = tab.title
            binding.tabUrl.text = tab.url

            binding.root.setOnClickListener {
                onTabClick(tab)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val binding = ItemTabBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(tabs[position])
    }

    override fun getItemCount() = tabs.size
}