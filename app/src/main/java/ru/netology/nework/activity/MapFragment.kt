package ru.netology.nework.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
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
class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private var placeMark: PlacemarkMapObject? = null

    val imageProvider: ImageProvider =
        ImageProvider.fromResource(requireContext(), R.drawable.ic_location_on_24)

    private var listener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            if (placeMark == null) {
                placeMark = binding.map.mapWindow.map.mapObjects.addPlacemark()
            }
            placeMark?.apply {
                geometry = point
                setIcon(imageProvider)
                isVisible = true
            }
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.mapWindow?.map?.move(CameraPosition(it, 10F, 0F, 0F))
            }
            userLocation.setObjectListener(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false
            mapWindow.map.addInputListener(listener)
            userLocation.setObjectListener(locationObjectListener)
        }

        binding.map.mapWindow.map.addInputListener(listener)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView = null
    }
}
