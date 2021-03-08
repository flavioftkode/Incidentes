package ipvc.estg.incidentes.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ipvc.estg.incidentes.R;
import ipvc.estg.incidentes.constructors.MyMarker;

@SuppressLint("InflateParams")
public class MarkerClusterRenderer extends DefaultClusterRenderer<MyMarker> implements GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;
    private LayoutInflater layoutInflater;
    private static final int MARKER_DIMENSION = 48;
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;
    private Context mycontext;


    public MarkerClusterRenderer(@NonNull Context context, GoogleMap map, ClusterManager<MyMarker> clusterManager) {
        super(context, map, clusterManager);
        mycontext = context;
        iconGenerator = new IconGenerator(context);
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);
        this.googleMap = map;
        layoutInflater = LayoutInflater.from(context);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnInfoWindowClickListener(this);
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomClusterItemInfoView());
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyMarker item, MarkerOptions markerOptions) {
        if(item.getStatus() == 1){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
        }else if(item.getStatus() == 2 || item.getStatus() == 3){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
        }else if(item.getStatus() == 0){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background));
        }
    }

    @Override
    protected void onClusterItemRendered(MyMarker clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        MyMarker myMarker = (MyMarker) marker.getTag();
       /* Intent intent = new Intent(mycontext, SingleActivity.class);
        intent.putExtra("position",myMarker.getPosition());
        intent.putExtra("latitude",myMarker.getPosition().latitude);
        intent.putExtra("longitude",myMarker.getPosition().longitude);
        intent.putExtra("location",myMarker.getLocation());
        intent.putExtra("date",myMarker.getDate());
        intent.putExtra("time",myMarker.getTime());
        intent.putExtra("status",myMarker.getStatus());
        intent.putExtra("description",myMarker.getDescription());
        intent.putExtra("id",myMarker.getId());
        intent.putExtra("number",myMarker.getNumber());
        intent.putExtra("photo",myMarker.getPhoto());
        intent.putExtra("photo_finish",myMarker.getPhoto_finish());

        mycontext.startActivity(intent);*/

    }

    private class MyCustomClusterItemInfoView implements GoogleMap.InfoWindowAdapter {

        private final View clusterItemView;

        MyCustomClusterItemInfoView() {
            clusterItemView = layoutInflater.inflate(R.layout.custominfowindow, null);
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            final MyMarker myMarker = (MyMarker) marker.getTag();
            if (myMarker == null) return clusterItemView;

            TextView status = clusterItemView.findViewById(R.id.status);
            Log.d("STATUS", String.valueOf(myMarker.getStatus()));
            if(myMarker.getStatus() == 1){
                clusterItemView.findViewById(R.id.number_color).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_green));
                status.setText("completo");
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_blue));
            }else if(myMarker.getStatus() == 2 || myMarker.getStatus() == 3){
                clusterItemView.findViewById(R.id.number_color).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_red));
                status.setText("progress");
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_red));
            }else if(myMarker.getStatus() == 0){
                clusterItemView.findViewById(R.id.number_color).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_green_dark));
                status.setText("recived");
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_red_dark));
            }else{
                clusterItemView.findViewById(R.id.number_color).setBackgroundColor(ContextCompat.getColor(mycontext, R.color.cpb_grey));
                status.setText("error");
                status.setTextColor(ContextCompat.getColor(mycontext, R.color.cpb_grey));
            }




            TextView name = clusterItemView.findViewById(R.id.name);
            TextView date = clusterItemView.findViewById(R.id.date);
            TextView location = clusterItemView.findViewById(R.id.location);
       /*     TextView description = clusterItemView.findViewById(R.id.description);*/
            name.setText(myMarker.getNumber());

            location.setText(myMarker.getLocation());
        /*    description.setText(myMarker.getDescription());*/

            try {
                Date date_time = new SimpleDateFormat("dd/MM/yyyy").parse(myMarker.getDate());
                String formattedDate = new SimpleDateFormat("dd MMMM yyyy").format(date_time);
                date.setText(formattedDate+ " "+myMarker.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return clusterItemView;
        }

        @Override
        public View getInfoContents(final Marker marker) {

            return null;
        }
    }
}