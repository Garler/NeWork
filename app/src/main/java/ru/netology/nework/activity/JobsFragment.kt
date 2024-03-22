package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentJobsBinding

@AndroidEntryPoint
class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }
}