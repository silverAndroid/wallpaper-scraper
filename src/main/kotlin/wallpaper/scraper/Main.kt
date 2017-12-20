package wallpaper.scraper

import com.google.api.client.json.jackson2.JacksonFactory
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val randomizer = Random()
        val config = loadConfig()

        val credentials = DriveScraper.authorize()
        val service = DriveScraper.getService(credentials)

        runBlocking {
            while (true) {
                val list = DriveScraper.getImageList(service)
                val coroutines = DriveScraper.downloadImages(service, list)
                var files: MutableList<File>
                files = coroutines.map { it.await() }.toMutableList()

                val startTime = LocalDateTime.now()
                val filesList = List(files.size) { files[it] }

                while (startTime < startTime.plus(Duration.ofDays(1))) {
                    if (files.isEmpty()) {
                        files = MutableList(filesList.size) { filesList[it] }
                    }

                    val index = randomizer.nextInt(files.size)
                    val randomFile = files[index]
                    files.removeAt(index)

                    setBackground(randomFile)
                    println("Setting wallpaper to ${randomFile.absolutePath}")
                    delay(10, TimeUnit.MINUTES)
                }
            }
        }
    }

    private fun loadConfig(): Config {
        val jsonConverter = JacksonFactory.getDefaultInstance()
        val inputStream = Main::class.java.getResourceAsStream("/config.json")
        return jsonConverter.fromInputStream(inputStream, Config::class.java)
    }
}