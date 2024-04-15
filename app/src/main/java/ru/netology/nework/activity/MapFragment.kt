package ru.netology.nework.activity

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentMapBinding

@AndroidEntryPoint
class MapFragment : Fragment(), UserLocationObjectListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var userLocation: UserLocationLayer
    private lateinit var placeMark: PlacemarkMapObject
    private val gson = Gson()
    private var inputListener: InputListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        MapKitFactory.initialize(requireContext())

        val imageProvider: ImageProvider =
            ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

        inputListener = object : InputListener {
            override fun onMapTap(map: com.yandex.mapkit.map.Map, point: Point) {
                if (::placeMark.isInitialized) {
                    map.mapObjects.remove(placeMark)
                }
                placeMark = map.mapObjects.addPlacemark()
                placeMark.apply {
                    geometry = point
                    setIcon(imageProvider)
                }
                placeMark.isDraggable = true
            }

            override fun onMapLongTap(map: com.yandex.mapkit.map.Map, point: Point) = Unit
        }

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false
            mapWindow.map.addInputListener(inputListener as InputListener)
            userLocation.setObjectListener(this@MapFragment)
        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.save -> {
                    if (::placeMark.isInitialized) {
                        setFragmentResult(
                            "mapFragment",
                            bundleOf("point" to gson.toJson(placeMark.geometry))
                        )
                    }
                    findNavController().navigateUp()
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

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputListener?.let {
            mapView.mapWindow.map.removeInputListener(it)
            inputListener = null
        }
    }

    override fun onObjectAdded(view: UserLocationView) = Unit

    override fun onObjectRemoved(view: UserLocationView) = Unit

    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
        userLocation.setAnchor(
            PointF(mapView.width * 0.5F, mapView.height * 0.5F),
            PointF(mapView.width * 0.5F, mapView.height * 0.5F)
        )
        mapView.mapWindow.map.move(
            CameraPosition(view.arrow.geometry, 16f, 0f, 0f)
        )
    }

}