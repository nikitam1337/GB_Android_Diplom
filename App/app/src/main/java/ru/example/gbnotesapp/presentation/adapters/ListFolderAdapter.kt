package ru.example.gbnotesapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.example.gbnotesapp.data.model.Folder
import ru.example.gbnotesapp.databinding.ItemFolderToListBinding

class ListFolderAdapter(
    private val onFolderClick: (Folder) -> Unit,
    private val onFolderLongClick: (Folder) -> Unit
) : ListAdapter<Folder, ListFolderViewHolder>(ListFolderDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFolderViewHolder {
        val binding = ItemFolderToListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListFolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListFolderViewHolder, position: Int) {
        val currentFolder = getItem(position)
        holder.bind(currentFolder)
        holder.itemView.setOnClickListener { onFolderClick(currentFolder) }
        holder.itemView.setOnLongClickListener {
            onFolderLongClick(currentFolder)
            true
        }
    }
}

class ListFolderDiffUtilCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder) =
        oldItem == newItem
}

class ListFolderViewHolder(
    private val binding: ItemFolderToListBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(folder: Folder) {
        binding.apply {
            folderName.text = folder.name
            checkmark.isVisible = folder.isSelected
        }
    }
}
