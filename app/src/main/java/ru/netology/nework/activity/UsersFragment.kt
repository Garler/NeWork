package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nework.R
import ru.netology.nework.adapter.OnInteractionListener
import ru.netology.nework.adapter.UserAdapter
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.viewmodel.UserViewModel

@AndroidEntryPoint
class UsersFragment : Fragment() {
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        val parentNavController = parentFragment?.parentFragment?.findNavController()
        val selectedUsers = mutableListOf<Int>()

        val userAdapter = UserAdapter(object : OnInteractionListener {

            override fun onSelectUser(userResponse: UserResponse) {
                selectedUsers.add(userResponse.id)
            }

            override fun onCardUser(feedItem: FeedItem) {
                parentNavController?.navigate(
                    R.id.action_mainFragment_to_detailUserFragment,
                )
            }
        })
        binding.listUsers.adapter = userAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.dataUser.collectLatest {
                    userAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshUsers.isRefreshing =
                    it.refresh is LoadState.Loading

                if (it.refresh is LoadState.Error) {
                    Snackbar.make(
                        binding.root,
                        R.string.connection_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.swipeRefreshUsers.setOnRefreshListener {
            userAdapter.refresh()
        }

        return binding.root
    }
}