plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Users\\os307\\OneDrive\\Desktop\\keystore\\signedkeys.jks")
            storePassword = "hepadell15z@"
            keyAlias = "key0"
            keyPassword = "hepadell15z@"
        }
    }
    compileSdkVersion(31)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.hfad.agendax"
        minSdkVersion(23)
        targetSdkVersion(31)
        versionCode = 3
        versionName = "2.0.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release"){
            isMinifyEnabled =  false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


}


dependencies {

    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    testImplementation ("junit:junit:4.13.2")

    val extVersion = "1.1.3"
    androidTestImplementation ("androidx.test.ext:junit:$extVersion")
    androidTestImplementation ("androidx.test.ext:junit-ktx:$extVersion")
    testImplementation ("androidx.test.ext:junit:$extVersion")

    val espressoVersion = "3.4.0"
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")
    testImplementation ("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")

    val coreVersion = "1.4.0"
    androidTestImplementation("androidx.test:core:$coreVersion")
    androidTestImplementation("androidx.test:core-ktx:$coreVersion")
    testImplementation("androidx.test:core:$coreVersion")
    testImplementation("androidx.test:core-ktx:$coreVersion")
    androidTestImplementation("androidx.test:runner:$coreVersion")

    //Ui Automator
    androidTestImplementation ("com.android.support.test.uiautomator:uiautomator-v18:2.1.3")


    //Fragment Testing
    val fragmentVersion = "1.4.0"
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")

    //Preference Library
    implementation("androidx.preference:preference:1.1.0")

    //Live data
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    //Fragment
    implementation ("androidx.fragment:fragment-ktx:1.3.6")

    //LeakCanary
    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.6")

    //ViewModel
    val lifecycleVersion = "2.2.0"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    //Recycler View
    val recyclerViewVersion = "1.2.1"
    implementation ("androidx.recyclerview:recyclerview:$recyclerViewVersion")

    //Kotlin coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    //Live Data
    val liveDataVersion = "2.3.1"
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$liveDataVersion")

    //Life Cycle
    val lifeCycleVersion = "2.3.1"
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifeCycleVersion")

    //Navigation
    val navVersion = "2.3.5"
    implementation ("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation ("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Room Library
    val roomVersion = "2.4.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    //optional - Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:$roomVersion")

    //Hilt Version
    val hiltVersion = rootProject.extra.get("hiltVersion")
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    kapt ("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation ("androidx.hilt:hilt-navigation-fragment:1.0.0")

    //Work Manager
    val workVersion = "2.7.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.hilt:hilt-work:1.0.0")
    // When using Kotlin.
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    //AdMob
    implementation ("com.google.android.gms:play-services-ads:20.5.0")


}

kapt {
    correctErrorTypes = true
}


