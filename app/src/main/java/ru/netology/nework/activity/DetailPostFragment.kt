package ru.netology.nework.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentDetailPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.util.loadAvatar
import ru.netology.nework.util.loadImage
import ru.netology.nework.viewmodel.PostViewModel
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailPostFragment : Fragment() {
    private lateinit var binding: FragmentDetailPostBinding
    private val postViewModel: PostViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private var placeMark: PlacemarkMapObject? = null
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailPostBinding.inflate(inflater, container, false)

        var post: Post? = null
        val imageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        postViewModel.postData.observe(viewLifecycleOwner) { postItem ->
            post = postItem
            with(binding) {
                authorAvatar.loadAvatar(postItem.authorAvatar)
                authorName.text = postItem.author
                lastWork.text = postItem.authorJob ?: getString(R.string.non_working)

                when (postItem.attachment?.type) {
                    AttachmentType.IMAGE -> {
                        imageContent.loadImage(postItem.attachment.url)
                        imageContent.isVisible = true
                    }

                    AttachmentType.VIDEO -> {
                        player = ExoPlayer.Builder(requireContext()).build().apply {
                            setMediaItem(MediaItem.fromUri(postItem.attachment.url))
                        }
                        videoContent.player = player
                        videoContent.isVisible = true
                    }

                    null -> {
                        imageContent.isVisible = false
                        videoContent.isVisible = false
                        player?.release()
                    }
                }

                datePublished.text =
                    postItem.published.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                textContent.text = postItem.content

                buttonLike.text = postItem.likeOwnerIds.size.toString()
                buttonLike.isChecked = postItem.likedByMe
                buttonUsers.text = postItem.mentionIds.size.toString()

                map.setNoninteractive(true)
                mapView = binding.map.apply {
                    val point =
                        if (postItem.coords != null) Point(
                            postItem.coords.lat,
                            postItem.coords.long
                        ) else null
                    if (point != null) {
                        if (placeMark == null) {
                            placeMark = mapWindow.map.mapObjects.addPlacemark()
                        }
                        placeMark?.apply {
                            geometry = point
                            setIcon(imageProvider)
                        }
                        mapWindow.map.move(
                            CameraPosition(
                                point,
                                17.0F,
                                0.0f,
                                0.0f
                            )
                        )
                    } else {
                        placeMark = null
                    }
                    isVisible = placeMark != null && point != null
                }
            }
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post?.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share))
                    startActivity(shareIntent)
                    true
                }

                else -> false
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        player?.apply {
            stop()
        }
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }
}
