plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt") // Tambahkan ini
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

android {
    namespace = "com.nanda.pertemuan12tugas2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nanda.pertemuan12tugas2"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            create("customDebugType") {
                isDebuggable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.room:room-runtime:2.6.1") // Downgrade ke 2.5.2
//    kapt("androidx.room:room-compiler:2.6.1") // Downgrade ke 2.5.2
    ksp("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.6.1") // Downgrade ke 2.5.2
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.github.bumptech.glide:glide:4.12.0") // Tambahan untuk memuat gambar

}