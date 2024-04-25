package ru.example.gbnotesapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.example.gbnotesapp.data.db.FolderRepository
import ru.example.gbnotesapp.data.db.NoteRepository
import ru.example.gbnotesapp.data.model.Folder
import ru.example.gbnotesapp.data.model.Note
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository
    ) : ViewModel() {

    // LiveData для всех папок
    private val _allNotesByFolder = MutableStateFlow<List<Note>>(listOf())
    val allNotesByFolder = _allNotesByFolder
//    noteRepository.getNotesBySelectedFolder(1)

    // LiveData для всех заметок
    private val _allFolders = MutableStateFlow<List<Folder>>(listOf())
    val allFolders = _allFolders
    init {
        viewModelScope.launch {
            _allFolders.value = folderRepository.getAllFolders()
            createMainFolder()
            _allNotesByFolder.value = noteRepository.getNotesBySelectedFolder(1)
        }
    }

    suspend fun changeListNote(idFolder: Int) {
        _allNotesByFolder.value = noteRepository.getNotesBySelectedFolder(idFolder)
    }

    // Функция для вставки новой заметки
    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    // Функция для вставки новой папки
    fun insertFolder(folder: Folder) = viewModelScope.launch {
        folderRepository.insert(folder)
        updateFolders()
    }

    private fun createMainFolder() {
        viewModelScope.launch {
            val mainFolderName = "Все"
            val firstFolder = folderRepository.getFolderById(1)
            if (firstFolder == null) {
                val mainFolder = Folder(id = null, name = mainFolderName, isSelected = true)
                insertFolder(mainFolder)
            }
        }
    }

    // Функция для обнавления
    fun updateFolders() {
        viewModelScope.launch {
            allFolders.value = folderRepository.getAllFolders()
        }
    }
}