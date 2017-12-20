package wallpaper.scraper

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.FileOutputStream
import java.io.InputStreamReader

object DriveScraper: Scraper<Credential, Drive> {
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val SCOPES = mutableListOf(DriveScopes.DRIVE)
    private val HTTP_TRANSPORT: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    private val DATA_STORE_DIR = java.io.File(System.getProperty("user.home"), ".credentials/wallpaper-scraper")
    private val DATA_STORE_FACTORY: FileDataStoreFactory = FileDataStoreFactory(DATA_STORE_DIR)
    private val folderPath = "./wallpapers"

    override fun authorize(): Credential {
        // Load client secrets.
        val inputStream = DriveScraper::class.java.getResourceAsStream("/client_secret.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build()
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    override fun getService(credentials: Credential): Drive = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
            .setApplicationName("Wallpaper Scraper")
            .build()

    override fun getImageList(api: Drive): List<Image> {
        val query = api.Files().list()
        query.orderBy = "name_natural"
        query.q = "\"0B6eB1SQ3pEV1Uzk5UV9aSmRScUU\" in parents and mimeType = \"image/png\""

        val files = query.execute()["files"] as List<File>
        return files.map { Image(it.id, it.name) }
    }

    override fun downloadImages(api: Drive, images: List<Image>): List<Deferred<java.io.File>> {
        return images.map {
            async {
                downloadImage(api, it)
            }
        }
    }

    private fun downloadImage(driveAPI: Drive, image: Image): java.io.File {
        createFolder(folderPath)
        val localFile = java.io.File("$folderPath/${image.name}")
        if (localFile.exists())
            return localFile

        localFile.createNewFile()

        val outputStream = FileOutputStream(localFile)
        driveAPI.Files().get(image.id).executeMediaAndDownloadTo(outputStream)
        println("Completed downloading ${image.name}")

        return localFile
    }

    private fun createFolder(path: String) {
        val folder = java.io.File(path)
        if (!folder.exists()) {
            folder.mkdir()
        }
    }
}