package wallpaper.scraper

import kotlinx.coroutines.experimental.runBlocking

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val credentials = Scraper.authorize()
        val service = Scraper.getDriveService(credentials)

        val list = Scraper.getImages(service)
        val coroutines = Scraper.downloadImages(service, list)
        runBlocking {
            coroutines.forEach { it.join() }
        }
    }
}