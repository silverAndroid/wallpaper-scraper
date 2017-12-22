package wallpaper.scraper

import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.Job

object ImgurScraper: Scraper<String, ImgurService> {
    val config: Config = kodein.instance(CONFIG_TAG)

    override fun authorize(): String = config.authProviders[0].clientID

    override fun getService(credentials: String): ImgurService {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImageList(api: ImgurService): List<Image> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadImages(api: ImgurService, images: List<Image>): List<Job> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}