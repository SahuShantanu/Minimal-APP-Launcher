package com.example.ui.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Podcasts
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.LauncherApp

object AppGlyphMapper {

    fun getGlyphForApp(app: LauncherApp): ImageVector {
        val label = app.normalizedLabel
        val pkg = app.packageName.lowercase()

        return when {
            // Browser (matches screenshot globe icon)
            label.contains("browser") || label.contains("chrome") || label.contains("firefox") ||
                label.contains("opera") || label.contains("edge") || label.contains("web") ||
                label.contains("safari") || pkg.contains("browser") || pkg.contains("chrome") ->
                Icons.Outlined.Language

            // Camera (matches screenshot camera icon)
            label.contains("camera") || pkg.contains("camera") ->
                Icons.Outlined.PhotoCamera

            // Files (matches screenshot folder icon)
            label.contains("file") || label.contains("folder") || label.contains("document") ||
                label.contains("explorer") || label.contains("archive") || pkg.contains("documentsui") ||
                pkg.contains("filemanager") ->
                Icons.Outlined.Folder

            // Mail (matches screenshot mail icon)
            label.contains("mail") || label.contains("email") || label.contains("gmail") ||
                label.contains("outlook") || pkg.contains("email") || pkg.contains("gmail") ->
                Icons.Outlined.Email

            // Maps (matches screenshot map icon)
            label.contains("map") || label.contains("navigation") || label.contains("waze") ||
                label.contains("transit") || label.contains("earth") || pkg.contains("maps") ->
                Icons.Outlined.Map

            // Music (matches screenshot music note icon)
            label.contains("music") || label.contains("audio") || label.contains("spotify") ||
                label.contains("sound") || label.contains("song") || pkg.contains("music") ->
                Icons.Outlined.MusicNote

            // Phone (matches screenshot phone icon)
            label.contains("phone") || label.contains("dialer") || label.contains("call") ||
                pkg.contains("dialer") || pkg.contains("telecom") ->
                Icons.Outlined.Phone

            // Settings (matches screenshot gear icon)
            label.contains("setting") || label.contains("config") || label.contains("preference") ||
                pkg.contains("settings") ->
                Icons.Outlined.Settings

            // Photos (matches screenshot image icon)
            label.contains("photo") || label.contains("gallery") || label.contains("picture") ||
                label.contains("image") || pkg.contains("gallery") || pkg.contains("photos") ->
                Icons.Outlined.Image

            // Clock (matches screenshot clock icon)
            label.contains("clock") || label.contains("alarm") || label.contains("timer") ||
                label.contains("stopwatch") || pkg.contains("deskclock") || pkg.contains("clock") ->
                Icons.Outlined.Schedule

            // Notes (matches screenshot note icon)
            label.contains("note") || label.contains("keep") || label.contains("memo") ||
                label.contains("todo") || label.contains("task") || pkg.contains("keep") ||
                pkg.contains("note") ->
                Icons.Outlined.Description

            // Calendar (matches screenshot calendar icon)
            label.contains("calendar") || label.contains("agenda") || label.contains("date") ||
                pkg.contains("calendar") ->
                Icons.Outlined.CalendarToday

            // Messages / SMS / Chat
            label.contains("message") || label.contains("sms") || label.contains("chat") ||
                label.contains("mms") || label.contains("signal") || label.contains("whatsapp") ||
                label.contains("telegram") || pkg.contains("messaging") || pkg.contains("mms") ->
                Icons.Outlined.Chat

            // Contacts / People
            label.contains("contact") || label.contains("people") || label.contains("address") ||
                pkg.contains("contacts") ->
                Icons.Outlined.Person

            // Calculator
            label.contains("calc") || pkg.contains("calculator") ->
                Icons.Outlined.Calculate

            // Store / Play Store
            label.contains("store") || label.contains("market") || label.contains("shop") ||
                pkg.contains("vending") ->
                Icons.Outlined.ShoppingBag

            // Video / Streaming
            label.contains("youtube") || label.contains("video") || label.contains("movie") ||
                label.contains("netflix") || pkg.contains("youtube") ->
                Icons.Outlined.PlayCircle

            // Weather
            label.contains("weather") || pkg.contains("weather") ->
                Icons.Outlined.WbSunny

            // Voice / Mic
            label.contains("voice") || label.contains("recorder") || label.contains("sound recorder") ||
                label.contains("mic") || pkg.contains("recorder") ->
                Icons.Outlined.Mic

            // Podcasts / Radio
            label.contains("podcast") || label.contains("radio") || pkg.contains("podcast") ->
                Icons.Outlined.Podcasts

            // Health / Fitness
            label.contains("health") || label.contains("fit") || label.contains("gym") ||
                label.contains("workout") ->
                Icons.Outlined.FitnessCenter

            // Games
            label.contains("game") || label.contains("play") || pkg.contains("game") ->
                Icons.Outlined.SportsEsports

            // Wallet / Finance / Bank
            label.contains("wallet") || label.contains("pay") || label.contains("bank") ||
                label.contains("money") ->
                Icons.Outlined.AccountBalanceWallet

            // News / Reader
            label.contains("news") || label.contains("feed") || label.contains("reader") ||
                label.contains("medium") ->
                Icons.Outlined.Article

            // Books
            label.contains("book") || label.contains("read") || label.contains("kindle") ->
                Icons.Outlined.MenuBook

            // Terminal / Dev
            label.contains("terminal") || label.contains("console") || label.contains("termux") ->
                Icons.Outlined.Terminal

            // Security
            label.contains("security") || label.contains("shield") || label.contains("antivirus") ->
                Icons.Outlined.Security

            // Cloud / Drive
            label.contains("drive") || label.contains("cloud") || label.contains("dropbox") ||
                label.contains("onedrive") ->
                Icons.Outlined.Cloud

            // Search
            label.contains("search") || label.contains("google") || pkg.contains("quicksearchbox") ->
                Icons.Outlined.Search

            // Compass / Tools
            label.contains("compass") || label.contains("tool") ->
                Icons.Outlined.Explore

            // Elegant fallback
            else -> Icons.Outlined.Widgets
        }
    }
}
