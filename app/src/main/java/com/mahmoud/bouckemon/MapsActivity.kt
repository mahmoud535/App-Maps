package com.mahmoud.bouckemon

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadpockemons()
        checkPermmison()
    }


    var ACCESSLOCATION=123
    fun checkPermmison(){

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.
                checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }

        GetUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if (grantResults[0]==PackageManager .PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun GetUserLocation() {
        Toast.makeText(this, "User location access on", Toast.LENGTH_LONG).show()
        //TODO: Will implement later
          //لكي نشغله
        val mylocation=mylocationlistener()
        val locationManager=getSystemService(Context.LOCATION_SERVICE)as LocationManager //كي يصل الي maneger اللي يخص الlocation
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,mylocation)
        //معناها ياlocationManager كل 3متر و3 منت تستدعي اinner class mylocationlistener:LocationListener
        val mythread=MyThread()
        mythread.start()
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Me")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))



    }
    /////////////////////////////////////////////////////////////////////////
    var mylocation:Location?=null //to read location
    //للوصول الي الجي بي اس
    //حتي نصل الي معلومات اlocationنكتب LocationListener
    inner class mylocationlistener:LocationListener{
        constructor(){
            mylocation=Location("me")
            mylocation!!.longitude=0.0
            mylocation!!.altitude=0.0
        }
        override fun onLocationChanged(location: Location?) {
            mylocation=location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

    }
    //لكي اعرف البيانات الللي علي الخريطه اعرف كلاس اسميه thread
    var oldlocation:Location?=null
    inner class MyThread:Thread{
        constructor():super(){
            //todo:see old location
            oldlocation=Location("oldlocation")
            oldlocation!!.longitude=0.0
            oldlocation!!.altitude=0.0
        }
        override fun run(){
            while (true){
                try {
                    if (oldlocation!!.distanceTo(mylocation)==0f){
                        continue
                    }
                    oldlocation=mylocation
                //add marker in sydeny and move the camera

                   mMap!!.clear()
               runOnUiThread {//لكي اصل الي الui من داخل threadلا زم اعرف فانكشن اسمها runonuiThread
                   val sydney=LatLng(mylocation!!.latitude,mylocation!!.longitude)
                   mMap.addMarker(MarkerOptions().position(sydney).title("Me")
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))


                   //show pockemon
                   for(i in 0..Listofpbckemons.size-1){
                       var newpockemon=Listofpbckemons[i]
                       if(newpockemon.isCatch==false){
                           val pocklocation=LatLng(newpockemon.location!!.latitude,newpockemon.location!!.longitude)
                           mMap.addMarker(MarkerOptions().position(pocklocation)

                               .title(newpockemon.name)
                               .snippet(newpockemon.des+", power:"+newpockemon.power)
                               .icon(BitmapDescriptorFactory.fromResource(newpockemon.image!!)))

                           if(mylocation!!.distanceTo(newpockemon.location)<2)
                           {
                               mypour+=newpockemon.power!!
                               newpockemon.isCatch=true
                               Listofpbckemons[i]=newpockemon
                               Toast.makeText(applicationContext,
                                   "You catch new pockemon your new pwoer is " + mypour,
                                   Toast.LENGTH_LONG).show()

                           }


                       }
                   }

               }

                   Thread.sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }

    var mypour:Double=0.0
    var Listofpbckemons=ArrayList<bockemon>()
    fun loadpockemons(){
        Listofpbckemons.add(bockemon(R.drawable.charmander,"charmander","charmander living in japan",55.0,37.7789994893035,-122.1554155))
        Listofpbckemons.add(bockemon(R.drawable.bulbasaur,"bulbasaur","bulbasaur living in usa",88.0,37.785524788899035,-122.4514784446))
        Listofpbckemons.add(bockemon(R.drawable.squirtle,"squirtle","squirtle living in iran",33.0,37.4862118955,-122.147826548955))
    }
}