package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        val childNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val childNavController = childNavHostFragment.navController
        binding.bottomNavigation.setupWithNavController(childNavController)


//Переход на Логин
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.user -> {
                    requireParentFragment()
                        .findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                    true
                }
                else -> false
            }
        }


        return binding.root
    }
}


