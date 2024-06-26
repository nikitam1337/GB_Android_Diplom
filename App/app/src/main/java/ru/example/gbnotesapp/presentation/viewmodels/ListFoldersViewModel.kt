package ru.example.gbnotesapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.example.gbnotesapp.data.db.FolderRepository
import ru.example.gbnotesapp.data.model.Folder
import javax.inject.Inject

class ListFoldersViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {
    private val _allFolders = MutableStateFlow<List<Folder>>(listOf())
    val allFolders = _allFolders

    init {
        viewModelScope.launch {
            _allFolders.value = folderRepository.getAllFolders()
        }
    }

    // Функция для вставки новой папки
    fun insert(folder: Folder) = viewModelScope.launch {
        folderRepository.insert(folder)
        updateFolders()
    }

    // Функция для обнавления
    private fun updateFolders() {
        viewModelScope.launch {
            allFolders.value = folderRepository.getAllFolders()
        }
    }

    fun deleteFolder(folder: Folder) = viewModelScope.launch {
        folderRepository.deleteFolder(folder)
        _allFolders.value = folderRepository.getAllFolders()
    }
}
