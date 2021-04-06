package com.example.matrixassignment.crossapplication.di

import com.example.matrixassignment.countriesdatascreen.network.CountriesDataNetworkService
import com.example.matrixassignment.countriesdatascreen.repository.CountriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * A dependency injection provider for the demo's different components
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://restcountries.eu/")
        .build()

    @Singleton
    @Provides
    fun provideCountriesNetworkService(): CountriesDataNetworkService {
        return retrofit.create(CountriesDataNetworkService::class.java)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoriesModule {

    @ViewModelScoped
    @Provides
    fun provideCountriesRepository(countriesDataNetworkService: CountriesDataNetworkService): CountriesRepository {
        return CountriesRepository(countriesDataNetworkService)
    }
}

