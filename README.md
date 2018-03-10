# Android app

# How to get the source

    git clone https://git.dev.uscki.nl/AppCKI/appcki-native-android.git -b development
    cd appcki-native-android

# How to build

Open Android Studio, als je nog een project open hebt, sluit het. Open nu een bestaand android studio project en zoek de directory op waar je het project hebt gecloned. Als het goed is doet android studio nu de rest.

In beide directories `app/src/master/res/values/` en `app/src/development/res/values` staat een bestand genaamd `api-template.xml`. Kopieer deze naar een bestand `api.xml` in dezelfde map, om de app te kunnen laten draaien.