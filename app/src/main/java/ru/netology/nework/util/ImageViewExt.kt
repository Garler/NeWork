package ru.netology.nework.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nework.R

fun ImageView.loadAvatar(url: String?) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_account_circle_24)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_account_circle_24)
        .timeout(10_000)
        .centerCrop()
        .into(this)
}