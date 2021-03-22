package ipvc.estg.incidentes.retrofit

import ipvc.estg.incidentes.entities.User
import retrofit2.Call
import retrofit2.http.*


interface EndPoints {

    @FormUrlEncoded
    @POST("/api/user/login")
    fun loginUser(@Field("payload") payload: String): Call<User>

    @GET("/users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

   /* @FormUrlEncoded
    @POST("/posts")
    fun postTest(@Field("title") first: String?): Call<OutputPost>*/
}