package ru.example.gbnotesapp.presentation.fragments

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.example.gbnotesapp.R
import ru.example.gbnotesapp.data.db.FolderRepository
import ru.example.gbnotesapp.data.db.NoteRepository
import ru.example.gbnotesapp.data.model.Folder
import ru.example.gbnotesapp.databinding.FragmentListFoldersBinding
import ru.example.gbnotesapp.presentation.ViewModelFactory
import ru.example.gbnotesapp.presentation.adapters.ListFolderAdapter
import ru.example.gbnotesapp.presentation.viewmodels.ListFoldersViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ListFoldersFragment : Fragment() {

    private var _binding: FragmentListFoldersBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var listFoldersViewModelFactory: ViewModelFactory
    private val listFoldersViewModel: ListFoldersViewModel by viewModels { listFoldersViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListFoldersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listFolderAdapter = ListFolderAdapter()
        binding.recyclerViewFolders.adapter = listFolderAdapter

        val layoutManagerFolders = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFolders.layoutManager = layoutManagerFolders

        // Обновляем список папок при изменении данных
        viewLifecycleOwner.lifecycleScope.launch {
            listFoldersViewModel.allFolders.collect { folders ->
                listFolderAdapter.submitList(folders)
            }
        }
        clickButtonBack()
        clickButtonNewFolder()
    }

    private fun clickButtonBack() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_ListFoldersFragment_to_MainFragment)
        }
    }

    private fun clickButtonNewFolder() {
        binding.newFolderButton.setOnClickListener {
            showNewFolderDialog()
        }
    }


    private fun showNewFolderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Новая папка")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(15)) // Ограничение на ввод названия папки 15 символами
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val folderName = input.text.toString()
            listFoldersViewModel.viewModelScope.launch {
                if (folderName.isNotEmpty() && !doesFolderExist(folderName)) {
                    createNewFolder(folderName)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Папка с таким названием уже существует",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private suspend fun doesFolderExist(folderName: String): Boolean {
        var exists = false
        listFoldersViewModel.allFolders.collect { folders ->
            exists = folders.any { it.name == folderName }
        }
        return exists
    }

    private fun createNewFolder(folderName: String) {
        if (folderName.isBlank()) {
            Toast.makeText(requireContext(), "Имя папки не может быть пустым", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val newFolder = Folder(null, folderName, 0, false)
        listFoldersViewModel.insert(newFolder)
    }

    private fun createFolderView(inflater: LayoutInflater, folder: Folder): View {
        // Создаем новый экземпляр представления папки
        val folderView =
            inflater.inflate(R.layout.item_folder_to_list, binding.recyclerViewFolders, false)

        // Заполняем данные папки
        val folderNameTextView = folderView.findViewById<TextView>(R.id.folderName)
        folderNameTextView.text = folder.name

        val isFolderSelected = folderView.findViewById<ImageView>(R.id.checkmark)
        isFolderSelected.isVisible = folder.isSelected

        return folderView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }
}
