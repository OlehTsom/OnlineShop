package com.example.onlineshop

import android.app.Application
import com.example.onlineshop.firebase.FirebaseCommon
import com.example.onlineshop.viewmodel.AddressViewModel
import com.example.onlineshop.viewmodel.AllOrdersViewModel
import com.example.onlineshop.viewmodel.BillingViewModel
import com.example.onlineshop.viewmodel.CardViewModel
import com.example.onlineshop.viewmodel.DetailsViewModel
import com.example.onlineshop.viewmodel.IntroductionViewModel
import com.example.onlineshop.viewmodel.LoginViewModel
import com.example.onlineshop.viewmodel.MainCategoryViewModel
import com.example.onlineshop.viewmodel.OrderViewModel
import com.example.onlineshop.viewmodel.ProfileViewModel
import com.example.onlineshop.viewmodel.RegisterViewModel
import com.example.onlineshop.viewmodel.SearchViewModel
import com.example.onlineshop.viewmodel.SupportViewModel
import com.example.onlineshop.viewmodel.UserAccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ShopApp : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@ShopApp))
        bind() from singleton { Firebase.firestore }
        bind() from singleton { FirebaseAuth.getInstance()}
        bind() from singleton {FirebaseCommon(instance(),instance())}
        bind() from singleton {  FirebaseStorage.getInstance().reference }

        bind() from provider { SupportViewModel(instance(),instance()) }
        bind() from provider { ProfileViewModel(instance(),instance()) }
        bind() from provider { AllOrdersViewModel(instance(),instance()) }
        bind() from provider { UserAccountViewModel(instance(),instance(),instance(),instance()) }
        bind() from provider { BillingViewModel(instance(),instance()) }
        bind() from provider { OrderViewModel(instance(),instance()) }
        bind() from provider { AddressViewModel(instance(),instance()) }
        bind() from provider { CardViewModel(instance(),instance(),instance()) }
        bind() from provider { DetailsViewModel(instance(),instance(),instance())}
        bind() from singleton { SearchViewModel(instance())}
        bind() from singleton { MainCategoryViewModel(instance()) }
        bind() from provider { RegisterViewModel(instance(),instance())}
        bind() from provider { LoginViewModel(instance()) }
        bind() from provider { IntroductionViewModel(this@ShopApp,instance()) }
    }
}