plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ichat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ichat"
        minSdk = 24
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    implementation ("com.hbb20:ccp:2.6.0")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.karumi:dexter:6.2.3")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("androidx.lifecycle:lifecycle-runtime:2.0.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.0.0")
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:2.0.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.github.yalantis:ucrop:2.2.6")
    implementation ("com.squareup.picasso:picasso:2.8")
}