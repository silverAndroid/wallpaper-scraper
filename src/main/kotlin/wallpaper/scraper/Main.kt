package wallpaper.scraper

import kotlinx.coroutines.experimental.runBlocking

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val credentials = DriveScraper.authorize()
        val service = DriveScraper.getService(credentials)

        val list = DriveScraper.getImageList(service)
        val coroutines = DriveScraper.downloadImages(service, list)
        runBlocking {
            coroutines.forEach { it.join() }
        }
    }
}