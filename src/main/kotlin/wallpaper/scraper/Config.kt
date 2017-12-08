package wallpaper.scraper

data class Config(val providers: List<Provider>, val wallpapersFolderPath: String)

data class Provider(val name: String, val images: List<String>)