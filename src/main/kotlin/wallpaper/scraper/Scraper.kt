package wallpaper.scraper

import kotlinx.coroutines.experimental.Job

interface Scraper<C, A> {
    fun authorize(): C
    fun getService(credentials: C): A
    fun getImageList(api: A): List<Image>
    fun downloadImages(api: A, images: List<Image>): List<Job>
}