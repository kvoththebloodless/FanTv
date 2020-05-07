# FanTv

## What it's about

A multiplatform, material themed application that seeks to provide a comprehensive look at the most trending and popular TV
Shows through ratings, reviews, news and trailers.

## Components

- App validates all input from TMDB and TVDB API. If data does not exist or is in the wrong format, the app logs this fact and does not crash.

- App includes support for accessibility. That includes content descriptions.

- App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.

- App provides a widget to provide relevant information to the user on the home screen.

- App builds from a clean repository checkout with no additional configuration.

- App builds and deploys using the installRelease Gradle task.

- App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.

- All app dependencies are managed by Gradle.

- App stores data locally by implementing a ContentProvider.



- App uses jobdispatcher and an IntentService to pulls or sends data to/from a TMDB and TVDB.

- It performs short duration, on-demand requests(such as search) using AsyncTask.

- App uses a Loader to move its data to its views.


![Screenshot_2017-04-03-23-36-16](https://user-images.githubusercontent.com/13608668/81304906-b2818100-904b-11ea-9386-58552b5b40ac.png)
![Screenshot_2017-04-03-23-36-33](https://user-images.githubusercontent.com/13608668/81304912-b3b2ae00-904b-11ea-9eb3-08bf7b35dd07.png)
![Screenshot_2017-04-03-23-36-18](https://user-images.githubusercontent.com/13608668/81304917-b44b4480-904b-11ea-8bfa-a161c5dcb74f.png)
![Screenshot_2017-04-03-23-35-36](https://user-images.githubusercontent.com/13608668/81304929-b8776200-904b-11ea-8f6f-82aa2d5a712b.png)
![Screenshot_2017-04-03-23-36-00](https://user-images.githubusercontent.com/13608668/81304932-b9a88f00-904b-11ea-9878-5a5695c0ab57.png)

