package wallpaper.scraper

import com.google.api.client.json.jackson2.JacksonFactory
import kotlinx.coroutines.experimental.runBlocking

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = loadConfig()
        val credentials = DriveScraper.authorize()
        val service = DriveScraper.getService(credentials)

        val list = DriveScraper.getImageList(service)
        val coroutines = DriveScraper.downloadImages(service, list)
        runBlocking {
            coroutines.forEach { it.join() }
        }
    }

    private fun loadConfig(): Config {
        val jsonConverter = JacksonFactory.getDefaultInstance()
        val inputStream = Main::class.java.getResourceAsStream("/config.json")
        return jsonConverter.fromInputStream(inputStream, Config::class.java)
    }
}