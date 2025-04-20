plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.project_mobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_mobile"
        minSdk = 24
        targetSdk = 34
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.sliderImage)
    implementation(libs.circleimageview)
    implementation(libs.circleindicator)
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.core)
    implementation(libs.zxing.android.embedded)
    testImplementation(libs.junit)
    implementation(libs.jbcrypt)
    implementation(libs.cloudinary.cloudinary.android)
    implementation(libs.stompprotocolandroid)
    implementation(libs.java.websocket)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}