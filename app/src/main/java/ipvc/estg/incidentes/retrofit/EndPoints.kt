package ipvc.estg.incidentes.retrofit

import ipvc.estg.incidentes.entities.MyMarker
import ipvc.estg.incidentes.entities.User
import retrofit2.Call
import retrofit2.http.*


interface EndPoints {

    @FormUrlEncoded
    @POST("/api/user/login")
    fun loginUser(@Field("payload") payload: String): Call<User>

    @GET("/api/event/get")
    fun getCluster(): Call<List<MyMarker>>
}