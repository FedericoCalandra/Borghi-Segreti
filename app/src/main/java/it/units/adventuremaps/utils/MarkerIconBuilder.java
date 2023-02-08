package it.units.adventuremaps.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import it.units.adventuremaps.models.Experience;

public class MarkerIconBuilder {

    private final Experience experience;

    public MarkerIconBuilder(Experience experience) {
        this.experience = experience;
    }

    public BitmapDescriptor buildDescriptor() {
        BitmapDescriptor descriptor;

        switch (experience.getType()) {
            case MOUNTAIN:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/MountainIcon.png");
                break;
            case NATURALISTIC_AREA:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/ForestIcon.png");
                break;
            case PANORAMIC_VIEW:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/PanoramicViewIcon.png");
                break;
            case POINT_OF_HISTORICAL_INTEREST:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/InfoIcon.png");
                break;
            case RESTAURANT:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/RestaurantIcon.png");
                break;
            case RIVER_WATERFALL:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/RiverIcon.png");
                break;
            case TYPICAL_FOOD:
                descriptor = BitmapDescriptorFactory.fromAsset("markers/BasketIcon.png");
                break;
            default:
                descriptor = BitmapDescriptorFactory.defaultMarker();
        }

        return descriptor;
    }

}
