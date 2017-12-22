package wallpaper.scraper

import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

object Main {
    val config: Config = kodein.instance(CONFIG_TAG)

    @JvmStatic
    fun main(args: Array<String>) {
        val randomizer = Random()

        val credentials = DriveScraper.authorize()
        val service = DriveScraper.getService(credentials)

        runBlocking {
            while (true) {
                val list = DriveScraper.getImageList(service)
                val coroutines = DriveScraper.downloadImages(service, list)
                var files = coroutines.map { it.await() }.toMutableList()

                val startTime = LocalDateTime.now()
                val restartTime = startTime.plus(Duration.ofDays(1))
                val filesList = List(files.size) { files[it] }

                while (startTime < restartTime) {
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
}