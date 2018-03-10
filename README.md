# Android app

# How to get the source

    git clone https://git.dev.uscki.nl/AppCKI/appcki-native-android.git -b development
    cd appcki-native-android

# How to build

Open Android Studio, als je nog een project open hebt, sluit het. Open nu een bestaand android studio project en zoek de directory op waar je het project hebt gecloned. Als het goed is doet android studio nu de rest.

Het belangrijkste is dat je in `app/src/master/res/values/api.xml` en `app/src/development/res/values/api.xml` een string resource zet met de naam apiurl die de url van de api heeft. Zonder dit zal je app niet compilen. Zie de appcki wiki voor meer info.
Een voorbeeld van dit bestand staat in de betreffende directory als `api.xml.template`. Deze template kan worden gekopieerd naar `api.xml` in dezelfde directory, waarna het aan jou is om te controleren of de gegeven URI up-to-date is.