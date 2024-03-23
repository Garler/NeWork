package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentDetailEventBinding

@AndroidEntryPoint
class DetailEventFragment : Fragment() {
    private lateinit var binding: FragmentDetailEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return binding.root
    }
}