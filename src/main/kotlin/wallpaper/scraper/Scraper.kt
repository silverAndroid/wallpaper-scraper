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
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.io.FileOutputStream
import java.io.InputStreamReader

object Scraper {
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val SCOPES = mutableListOf(DriveScopes.DRIVE)
    private val HTTP_TRANSPORT: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    private val DATA_STORE_DIR = java.io.File(System.getProperty("user.home"), ".credentials/wallpaper-scraper")
    private val DATA_STORE_FACTORY: FileDataStoreFactory = FileDataStoreFactory(DATA_STORE_DIR)
    private val folder = "./wallpapers"

    fun authorize(): Credential {
        // Load client secrets.
        val inputStream = Scraper::class.java.getResourceAsStream("/client_secret.json")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build()
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    fun getDriveService(credential: Credential): Drive = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName("Anime Wallpaper Scraper")
            .build()

    fun getImages(driveAPI: Drive): FileList {
        val query = driveAPI.Files().list()
        query.orderBy = "name_natural"
        query.q = "\"0B6eB1SQ3pEV1Uzk5UV9aSmRScUU\" in parents and mimeType = \"image/png\""

        return query.execute()
    }

    fun downloadImages(driveAPI: Drive, fileList: FileList): List<Job> {
        val files = fileList["files"] as List<File>
        return files.map {
            launch {
                downloadImage(driveAPI, it)
            }
        }
    }

    private fun downloadImage(driveAPI: Drive, file: File) {
        createFolder(folder)
        val localFile = java.io.File("$folder/${file.name}")
        localFile.createNewFile()

        val outputStream = FileOutputStream(localFile)
        driveAPI.Files().get(file.id).executeMediaAndDownloadTo(outputStream)
        println("Completed downloading ${file.name}")
    }

    private fun createFolder(path: String) {
        val folder = java.io.File(path)
        if (!folder.exists()) {
            folder.mkdir()
        }
    }
}