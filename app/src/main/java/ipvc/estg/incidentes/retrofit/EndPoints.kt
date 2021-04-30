package ipvc.estg.incidentes.retrofit

import ipvc.estg.incidentes.entities.Event
import ipvc.estg.incidentes.entities.MyMarker
import ipvc.estg.incidentes.entities.User
import retrofit2.Call
import retrofit2.http.*


interface EndPoints {

    @FormUrlEncoded
    @POST("/api/user/login")
    fun loginUser(@Field("payload") payload: String): Call<User>

    @FormUrlEncoded
    @POST("/api/event/get")
    fun getCluster(@Field("payload") payload: String): Call<List<MyMarker>>

    @FormUrlEncoded
    @POST("/api/event/insert")
    fun insertEvent(@Field("payload") payload: String,@Header("Authorization") auth : String ): Call<Event>

    @FormUrlEncoded
    @POST("/api/event/update")
    fun updateEvent(@Field("payload") payload: String,@Header("Authorization") auth : String ): Call<Event>

   @FormUrlEncoded
   @POST("/api/event/delete")
   fun deleteEvent(@Field("id") id: String,@Field("user_id") user_id: String,@Header("Authorization") auth : String ): Call<Event>
}