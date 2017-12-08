package wallpaper.scraper

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val credentials = Scraper.authorize()
        val service = Scraper.getDriveService(credentials)

        val list = Scraper.getImages(service)
        Scraper.downloadImages(service, list)
    }
}