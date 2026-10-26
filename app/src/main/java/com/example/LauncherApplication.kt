package com.example

import android.app.Application
import com.example.data.AppRepository
import com.example.data.IconRepository
import com.example.data.SettingsRepository
import com.example.domain.ReconcileFavoritesUseCase
import com.example.domain.SearchAppsUseCase
import com.example.domain.SortAppsUseCase
import com.example.system.AppLaunchManager
import com.example.system.HomeRoleHandler

class LauncherApplication : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var appRepository: AppRepository
        private set

    lateinit var iconRepository: IconRepository
        private set

    lateinit var appLaunchManager: AppLaunchManager
        private set

    lateinit var homeRoleHandler: HomeRoleHandler
        private set

    lateinit var searchAppsUseCase: SearchAppsUseCase
        private set

    lateinit var sortAppsUseCase: SortAppsUseCase
        private set

    lateinit var reconcileFavoritesUseCase: ReconcileFavoritesUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        settingsRepository = SettingsRepository(this)
        appRepository = AppRepository(this)
        iconRepository = IconRepository(this)
        appLaunchManager = AppLaunchManager()
        homeRoleHandler = HomeRoleHandler(this)
        searchAppsUseCase = SearchAppsUseCase()
        sortAppsUseCase = SortAppsUseCase()
        reconcileFavoritesUseCase = ReconcileFavoritesUseCase()
    }

    override fun onTerminate() {
        super.onTerminate()
        appRepository.cleanup()
    }

    companion object {
        lateinit var instance: LauncherApplication
            private set
    }
}
