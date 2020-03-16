import com.google.gson.annotations.SerializedName

data class Json4Kotlin_Base (
	@SerializedName("status") val status : Status,
	@SerializedName("data") val data : List<Data>
)