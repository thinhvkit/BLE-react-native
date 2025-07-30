package com.myprotect.projectx.di

import app.cash.sqldelight.db.SqlDriver
import com.myprotect.projectx.ble.provideBLEManager
import com.myprotect.projectx.domain.core.AppDataStore
import com.myprotect.projectx.domain.core.AppDataStoreManager
import com.myprotect.projectx.domain.core.LocaleManager
import com.myprotect.projectx.data.datasource.network.httpClient
import com.myprotect.projectx.data.datasource.database.SqlDelightDriverFactory
import com.myprotect.projectx.data.datasource.database.createDatabase
import com.myprotect.projectx.domain.interactors.authentication.CheckTokenInteractor
import com.myprotect.projectx.domain.interactors.authentication.LoginInteractor
import com.myprotect.projectx.domain.interactors.authentication.LogoutInteractor
import com.myprotect.projectx.domain.interactors.culture.FetchCultureInteractor
import com.myprotect.projectx.domain.interactors.events.CreateEventInteractor
import com.myprotect.projectx.domain.interactors.language.FetchTranslationInteractor
import com.myprotect.projectx.domain.interactors.language.GelAllTranslationInteractor
import com.myprotect.projectx.domain.interactors.profile.GetUserProfileInteractor
import com.myprotect.projectx.domain.interactors.profile.UpdateUserProfileInteractor
import com.myprotect.projectx.domain.interactors.risk_message.GetRiskMessagesInteractor
import com.myprotect.projectx.domain.interactors.risk_message.UpdateRiskMessageInteractor
import com.myprotect.projectx.domain.interactors.setting.FetchDeviceSettingInteractor
import com.myprotect.projectx.common.Context
import com.myprotect.projectx.presentation.SharedViewModel
import com.myprotect.projectx.presentation.token_manager.TokenManager
import com.myprotect.projectx.presentation.ui.device_setup.view_model.DeviceSetupViewModel
import com.myprotect.projectx.presentation.ui.login.step1.view_model.LoginEmailViewModel
import com.myprotect.projectx.presentation.ui.login.step2.view_model.LoginEmailOTPViewModel
import com.myprotect.projectx.presentation.ui.login.step3.view_model.LoginPhoneViewModel
import com.myprotect.projectx.presentation.ui.login.step4.view_model.LoginPhoneOTPViewModel
import com.myprotect.projectx.presentation.ui.main.view_model.MainViewModel
import com.myprotect.projectx.presentation.ui.navigation_drawer.view_model.NavigationDrawerViewModel
import com.myprotect.projectx.presentation.ui.onboarding.language_and_timezone.view_model.LanguageAndTimezoneViewModel
import com.myprotect.projectx.presentation.ui.onboarding.medical_details.view_model.MedicalDetailsViewModel
import com.myprotect.projectx.presentation.ui.onboarding.personal_details.view_model.PersonalDetailsViewModel
import com.myprotect.projectx.presentation.ui.onboarding.view_model.OnboardingViewModel
import com.myprotect.projectx.presentation.ui.permission.view_model.PermissionViewModel
import com.myprotect.projectx.presentation.ui.risk_message.detail.view_model.RiskMessageDetailViewModel
import com.myprotect.projectx.presentation.ui.risk_message.list.view_model.RiskMessageViewModel
import com.myprotect.projectx.presentation.ui.setting.view_model.SettingViewModel
import com.myprotect.projectx.presentation.ui.splash.view_model.SplashViewModel
import com.myprotect.projectx.presentation.ui.working_status.view_model.WorkingStatusViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(context: Context) = module {
    single<SqlDriver> {
        SqlDelightDriverFactory().createDriver(context)
    }
    single {
        createDatabase(driver = get())
    }
    single { Json { isLenient = true; ignoreUnknownKeys = true } }
    single {
        httpClient(get(), get())
    }
    single<AppDataStore> { AppDataStoreManager(context) }
    factory { LoginInteractor(get(), get()) }
    factory { CheckTokenInteractor(get()) }
    factory { LogoutInteractor(get()) }
    factory { CreateEventInteractor(get(), get()) }
    factory { GetRiskMessagesInteractor(get()) }
    factory { FetchTranslationInteractor(get(), get()) }
    factory { GelAllTranslationInteractor(get()) }
    factory { UpdateRiskMessageInteractor(get(), get()) }
    factory { GetUserProfileInteractor(get(), get(), get(), get()) }
    factory { UpdateUserProfileInteractor(get(), get()) }
    factory { FetchCultureInteractor(get(), get()) }
    factory { FetchDeviceSettingInteractor(get(), get()) }

    factory { SharedViewModel(get(), get(), get(), get(), get(), get()) }
    factory { LoginEmailViewModel() }
    factory { LoginEmailOTPViewModel() }
    factory { LoginPhoneViewModel() }
    factory { LoginPhoneOTPViewModel(get()) }
    factory { PermissionViewModel(get()) }
    factory { NavigationDrawerViewModel(get(), get()) }
    factory { PersonalDetailsViewModel(get(), get()) }
    factory { MedicalDetailsViewModel() }
    factory { params ->
        LanguageAndTimezoneViewModel(params[0], get(), get(), get(), get())
    }
    factory { OnboardingViewModel(get()) }
    factory { RiskMessageViewModel(get()) }
    factory { RiskMessageDetailViewModel(get()) }
    factory { MainViewModel(get()) }
    factory { SettingViewModel(get(), get(), get()) }
    factory { WorkingStatusViewModel(get()) }
    factory { SplashViewModel(get()) }

    single { TokenManager(get()) }
    single { LocaleManager(get(), get(), context) }

    single { DeviceSetupViewModel(provideBLEManager(context = context), get()) }
}

internal expect fun platformModule(logEnable: Boolean = true): Module

internal sealed interface Platform {
    data object Android : Platform
    data object Ios : Platform
}
