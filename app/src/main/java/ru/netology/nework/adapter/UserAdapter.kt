package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.DiffCallback
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.util.loadAvatar

interface OnInteractionListener {
    fun onCardUser(feedItem: FeedItem)
    fun onSelectUser(userResponse: UserResponse)
}

class UserAdapter(
    private val onInteractionListener: OnInteractionListener,

) : PagingDataAdapter<FeedItem, UserViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position) as UserResponse
        holder.bind(item)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserResponse) {
        with(binding) {
            authorName.text = user.name
            authorLogin.text = user.login

            authorAvatar.loadAvatar(user.avatar)

            checkBox.setOnClickListener {
                onInteractionListener.onSelectUser(user)
            }

            cardUser.setOnClickListener {
                onInteractionListener.onCardUser(user)
            }
        }
    }
}