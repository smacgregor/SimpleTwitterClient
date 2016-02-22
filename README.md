# Project 3 - Simple Twitter Client

Simple Twitter Client is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: 20 hours spent in total

## User Stories

The following **required** functionality is completed:

* [X]	User can **sign in to Twitter** using OAuth login
* [X]	User can **view tweets from their home timeline**
  * [X] User is displayed the username, name, and body for each tweet
  * [X] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
  * [X] User can view more tweets as they scroll with [infinite pagination](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView). Number of tweets is unlimited.
    However there are [Twitter Api Rate Limits](https://dev.twitter.com/rest/public/rate-limiting) in place.
* [X] User can **compose and post a new tweet**
  * [X] User can click a “Compose” icon in the Action Bar on the top right
  * [X] User can then enter a new tweet and post this to twitter
  * [X] User is taken back to home timeline with **new tweet visible** in timeline

The following **optional** features are implemented:

* [X] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [X] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [X] User can **pull down to refresh tweets timeline**
* [ ] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
* [X] User can tap a tweet to **open a detailed tweet view**
* [X] User can **select "reply" from detail view to respond to a tweet**
* [X] Improve the user interface and theme the app to feel "twitter branded"

The following **bonus** features are implemented:

* [X] User can see embedded image media within the tweet detail view
* [X] User can watch embedded video within the tweet
* [X] Compose tweet functionality is build using modal overlay
* [X] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [X] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce view boilerplate.
* [X] Leverage the popular [GSON library](http://guides.codepath.com/android/Using-Android-Async-Http-Client#decoding-with-gson-library) to streamline the parsing of JSON data.
* [X] [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter for all lists of tweets.
* [X] Move the "Compose" action to a [FloatingActionButton](https://github.com/codepath/android_guides/wiki/Floating-Action-Buttons) instead of on the AppBar.
* [X] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.
* [X] Used the Ribot style guide - https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md

The following **additional** features are implemented:

* [X] Created a TwitterManager that encapsulates twitter logic and reduce code smells in the TimelineActivity. The twitter manager interacts with the twitter client.
* [X] Added functional action buttons for reply, retweet and favorite to both the timeline view and the details activity for every tweet.
* [X] Error handling for offline mode and failed network requests
* [X] The floating action bar disappears as you scroll the recylcer view.
* [X] Use the verify_credentials end point to get and store a current user object
* [X] Use high res profile pic images by using the _bigger version for user profile pictures
* [X] Add a back button to the tweet details screen
* [X] Round media images embedded inside of tweets like twitter does
* [X] Make liberal use of include XML files to re-use XML layouts between the timeline and the activity for rendering tweets. For instance I created an XML layout for the tweet action buttons (reply, retweet, favorite) and then included that in the tweet item for the RecyclerView and in the tweet details layout.
* [X] RecyclerView uses a heterogenous layout - one for tweets and one for tweets with media. XML includes and ViewHolder inheritance were used to minimize code bloat with the two layouts.
* [X] After posting a new tweet - auto scroll the recycler view so the new post is in view
* [X] Preserve bitmap quality when using Glide
* [X] Add an adapter animation (AlphaInAnimationAdapter) using the RecyclerView animations library which I thought looked pretty slick.

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='https://cloud.githubusercontent.com/assets/1521460/13209930/bfe51f4c-d8de-11e5-9325-4160cd9b39c3.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

I ran into a lot of problems getting ActiveAndroid to play nice with the nested class hierarchies introduced by relying on GSON to build my models. I came close and hope to finish that branch off tomorrow but it wasn't going to make tonights 10pm deadline.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [Parceler](https://github.com/johncarl81/parceler) - Remove boilerplate around making model objects parcelable
- [RecyclerView Animators](https://github.com/wasabeef/recyclerview-animators) - Make RecyclerView animations easy!
- [Gson](https://github.com/google/gson) - streamline JSON parsing into models
- [Butterknife](http://jakewharton.github.io/butterknife/) - Remove view binding boilerplate
- [RoundedImageView](https://github.com/vinc3m1/RoundedImageView) - For rounding profile pictures and rich media in the twitter timeline

## License

    Copyright 2016 Scott MacGregor

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
