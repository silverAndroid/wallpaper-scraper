package wallpaper.scraper

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type

val kodein = Kodein {
    bind<Config>(CONFIG_TAG) with singleton { loadConfig() }
    bind<Gson>(GSON_TAG) with singleton { Gson() }
}
val GSON_TAG = "gson"
val CONFIG_TAG = "config"

private fun loadConfig(): Config {
    val jsonConverter = kodein.instance<Gson>(GSON_TAG)

    val configStream = Main::class.java.getResourceAsStream("/config.json")
    val config = jsonConverter.fromJson(configStream, Config::class.java)

    val authStream = Main::class.java.getResourceAsStream("/auth.json")
    val auth = jsonConverter.fromJson(authStream, Array<AuthProvider>::class.java)
    config.authProviders = auth.toList()

    return config
}

fun <T> Gson.fromJson(inputStream: InputStream, clazz: Class<T>) = fromJson<T>(JsonReader(InputStreamReader(inputStream)), clazz)!!