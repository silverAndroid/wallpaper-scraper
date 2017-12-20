package wallpaper.scraper

import java.io.File


private val OS: String = System.getProperty("os.name")
val osType: OSType = getOperatingSystemType(OS)

enum class OSType {
    Windows, MacOS, Linux, Other
}

fun setBackground(file: File) {
    when (osType) {
        OSType.Windows -> TODO()
        OSType.MacOS -> TODO()
        OSType.Linux -> Runtime.getRuntime().exec("gsettings set org.gnome.desktop.background picture-uri file://${file.absolutePath}")
        OSType.Other -> TODO()
    }
}

private fun getOperatingSystemType(os: String): OSType = when {
    os.contains("mac") || os.contains("darwin") -> OSType.MacOS
    os.contains("win") -> OSType.Windows
    os.contains("nux") -> OSType.Linux
    else -> OSType.Other
}