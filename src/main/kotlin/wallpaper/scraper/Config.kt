package wallpaper.scraper

import com.google.gson.annotations.SerializedName

class Config(val providers: List<Provider>, val wallpapersFolderPath: String) {
    lateinit var authProviders: List<AuthProvider>
}

data class Provider(val name: String, val images: List<String>)

class AuthProvider(
        val name: String,
        @SerializedName("client_id") val clientID: String,
        @SerializedName("client_secret") val clientSecret: String,
        private val path: String
) {
//    val stream: InputStream?
}