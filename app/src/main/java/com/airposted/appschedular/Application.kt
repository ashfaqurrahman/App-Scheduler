package com.airposted.appschedular

import android.app.Application
import com.airposted.appschedular.data.db.AppDatabase
import com.airposted.appschedular.data.preferences.PreferenceProvider
import com.airposted.appschedular.view.home.HomeViewModelFactory
import com.airposted.appschedular.data.repositories.HomeRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import java.util.*

class Application : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@Application))

        bind() from singleton { PreferenceProvider(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { HomeRepository(instance(), instance()) }
        bind() from provider { HomeViewModelFactory(instance()) }
    }
}