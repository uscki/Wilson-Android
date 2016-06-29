# Android app

# how to get teh source

    git clone https://github.com/uscki/appcki-native-android
    cd appcki-native-android
    git submodule init
    git submodule update

# how to build

Open Android Studio, als je nog een project open hebt, sluit het. Open nu een bestaand android studio project en zoek de directory op waar je het project hebt gecloned. Als het goed is doet android studio nu de rest.

Het belangrijkste is dat je in app/src/master/res/values/api.xml en app/src/development/res/values/api.xml een string resource zet met de naam apiurl die de url van de api heeft. Zonder dit zal je app niet compilen. Zie de appcki wiki voor meer info.
