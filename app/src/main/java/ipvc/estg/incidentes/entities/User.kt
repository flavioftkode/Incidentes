package ipvc.estg.incidentes.entities


data class User(
    val id: Int,
    val name: String,
    val email: String,
    val _token: String,
)
