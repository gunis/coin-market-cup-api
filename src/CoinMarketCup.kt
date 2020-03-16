import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.IllegalArgumentException

fun main(args: Array<String>) {
    if (args.isEmpty()) throw IllegalArgumentException("API_KEY is missing")

    val apiKey = args[0]
    val url: String = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest" + "?" +
            listOf(
                Pair("start", "1"),
                Pair("limit", "3"),
                Pair("convert", "EUR")
            ).joinToString("&") { (k, v) -> "$k=$v" }

    try {
        val json = makeAPICall(url, apiKey)

        val response = Gson().fromJson(json, Json4Kotlin_Base::class.java)

        val incomingDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val requestDateTime = LocalDateTime.parse(response.status.timestamp, incomingDateTimeFormatter)
        println("----------------------------------")
        println("Info from: ${requestDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}")
        println("----------------------------------")

        response.data.forEach { data: Data ->
            println("Name: ${data.name}")
            println("Symbol: ${data.symbol}")
            println("Max supply: ${data.max_supply}")
            println("Total supply: ${data.total_supply}")
            println("Price (EUR): ${data.quote.eUR.price}")
            println("Market Cap: ${data.quote.eUR.market_cap}")
            println("----------------------------------")
        }

    } catch (e: IOException) {
        println("Error: cannot access content - $e")
    } catch (e: URISyntaxException) {
        println("Error: Invalid URL $e")
    }
}

  fun makeAPICall(urlString:String, apiKey:String):String {
      val responseContent: String
      val url = URL(urlString)
      val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

      urlConnection.setRequestProperty("Accept", "application/json")
      urlConnection.setRequestProperty("X-CMC_PRO_API_KEY", apiKey)
      try {
          val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
          responseContent = `in`.bufferedReader().use(BufferedReader::readText)
      } finally {
          urlConnection.disconnect()
      }

    return responseContent
  }