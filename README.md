# Project 4 - Simple Twitter Client Part II

Simple Twitter Client is an android app that allows a user to view home and mentions timelines, view user profiles with user timelines, as well as compose and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: 15 hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] The app includes **all required user stories** from Week 3 Twitter Client
* [X] User can **switch between Timeline and Mention views using tabs**
  * [X] User can view their home timeline tweets.
  * [X] User can view the recent mentions of their username.
* [X] User can navigate to **view their own profile**
  * [X] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [X] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [X] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [X] Profile view includes that user's timeline
* [X] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [ ] User can view following / followers list through the profile
* [X] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [X] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [X] User can **"reply" to any tweet on their home timeline**
  * [X] The user that wrote the original tweet is automatically "@" replied in compose
* [X] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [X] User can take favorite (and unfavorite) or retweet actions on a tweet
* [X] Improve the user interface and theme the app to feel twitter branded
* [ ] User can **search for tweets matching a particular query** and see results

* [X] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [X] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [X] User can **pull down to refresh tweets timeline**
* [X] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.

The following **bonus** features are implemented:

* [X] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [X] Apply the popular [Butterknife annotation library](http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife) to reduce view boilerplate.
* [ ] User can view their direct messages (or send new ones)
* [X] User can see embedded image media within the tweet detail view
* [X] User can watch embedded video within the tweet
* [X] Compose tweet functionality is build using modal overlay
* [X] Leverage the popular [GSON library](http://guides.codepath.com/android/Using-Android-Async-Http-Client#decoding-with-gson-library) to streamline the parsing of JSON data.
* [X] [Leverage RecyclerView](http://guides.codepath.com/android/Using-the-RecyclerView) as a replacement for the ListView and ArrayAdapter for all lists of tweets.
* [X] Move the "Compose" action to a [FloatingActionButton](https://github.com/codepath/android_guides/wiki/Floating-Action-Buttons) instead of on the AppBar.
* [X] Replace Picasso with [Glide](http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en) for more efficient image rendering.

The following **additional** features are implemented:

* [X] Created a User profile fragment to reduce the responsibilities of the profile fragment
* [X] Implemented a progress bar at the end of the RecyclerView using a heterogeneous RecyclerView where one of the view types is the progress bar. 
* [X] When viewing a user's profile page, include their twitter wallpaper as the background header.
* [X] Used Nate's smart fragment pager adapter class!
* [X] Used the Ribot style guide - https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md
* [X] Created a TwitterManager that encapsulates twitter logic and reduce code smells in the TimelineActivity. The twitter manager interacts with the twitter client.
* [X] Added functional action buttons for reply, retweet and favorite to both the timeline view and the details activity for every tweet.
* [X] Error handling for offline mode and failed network requests
* [X] The floating action bar disappears as you scroll the recylcer view.
* [X] Use the verify_credentials end point to get and store a current user object
* [X] Use high res profile pic images by using the _bigger version for user profile pictures
* [X] Add a back button to the tweet details screen
* [X] Add a back button to the profile activity
* [X] Round media images embedded inside of tweets like twitter does
* [X] Make liberal use of include XML files to re-use XML layouts between the timeline and the activity for rendering tweets. For instance I created an XML layout for the tweet action buttons (reply, retweet, favorite) and then included that in the tweet item for the RecyclerView and in the tweet details layout.
* [X] RecyclerView uses a heterogenous layout - one for tweets,  one for tweets with media, one for the progress bar. XML includes and ViewHolder inheritance were used to minimize code bloat with the two layouts.
* [X] After posting a new tweet - auto scroll the recycler view so the new post is in view
* [X] Preserve bitmap quality when using Glide
* [X] Add an adapter animation (AlphaInAnimationAdapter) using the RecyclerView animations library which I thought looked pretty slick.

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='https://cloud.githubusercontent.com/assets/1521460/13386469/87ea369e-de61-11e5-8c2f-d18dbe89f1c2.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

Here's a walkthrough of offline mode:

<img src='https://cloud.githubusercontent.com/assets/1521460/13386488/cfd23dda-de61-11e5-9efb-50da3954f225.gif' title='Offline Video Walkthrough' width='' alt='Offline Walkthrough'/>

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

This assignment went fairly smoothly. I liked how we got to build on top of the week 3 assignment!

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
