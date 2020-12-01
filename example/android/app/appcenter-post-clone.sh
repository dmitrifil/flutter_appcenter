#!/usr/bin/env bash
#Place this script in project/android/app/

cd ..

# fail if any command fails
set -e
# debug log
set -x

cd ..
git clone -b beta https://github.com/flutter/flutter.git
export PATH=`pwd`/flutter/bin:$PATH

flutter clean

# accepting all licenses
yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

flutter channel stable
flutter doctor -v --android-licenses

echo "Installed flutter to `pwd`/flutter"

# build APK
flutter build apk --flavor dev

# if you need build bundle (AAB) in addition to your APK, uncomment line below and last line of this script.
flutter build appbundle --flavor pr

# copy the APK where AppCenter will find it
mkdir -p android/app/build/outputs/apk/; mv build/app/outputs/apk/dev/release/app-dev-release.apk $_

# copy the AAB where AppCenter will find it
mkdir -p android/app/build/outputs/bundle/; mv build/app/outputs/bundle/prRelease/app-pr-release.aab $_
