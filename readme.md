# Hidden Pearls
## Hugbúnaðarverkefni 2 - HBV601G
## Team 1

## Members

- Kjartan Óli Ágústsson -> koa20@hi.is
- María Orradóttir -> mao44@hi.is
- Svana Kristín Elísdóttir -> ske12@hi.is
- Sverrir Sigfússon -> svs37@hi.is

## Description

Hidden Pearls is a Android app continuation of our web based project of the same name from last semester. The aim of the
app is to help tourists in Iceland discover places that are out of the common path and not swarmed with other tourists while
on their trip in Iceland. Find places by searching for them directly, using the mobile device to see what's close to you, favorite
places that appeal to you and read up on them, while seeing how many average weekly visitors it draws.

## Execution

To compile and run on connected Android device, run the following commands:
- `cd HiddenPearls`
- `./gradlew build`
- `adb install app/build/outputs/apk/debug/app-debug.apk`

To run the project in Android Studio:
- Open the Hidden Pearls folder in Android Studio
- Push Run -> Run 'app' to run the app in the connected emulator

## Features

| Feature                         | Description                                         |
| :------------------------------ | :-------------------------------------------------- | 
| House icon                      | Homepage with Top Pearls and Worst Traps            | 
| List icon                       | A list of all the locations on Hidden Pearls        | 
| Search icon                     | Type in a specific location                         | 
| Heart icon                      | Favorites stores the locations you favorite         | 
| Location icon                   | Search for locations near you with GPS              | 
| Tap on location                 | Read the description about the location you choose	| 
| Tap on heart icon in location   | Add location to favorites                           | 

While on the the home screen, shake your device for a surprise!
