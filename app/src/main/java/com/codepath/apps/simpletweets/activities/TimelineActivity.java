package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterManager;
import com.codepath.apps.simpletweets.adapters.TweetsAdapter;
import com.codepath.apps.simpletweets.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity
        implements FloatingActionButton.OnClickListener,
        TweetsAdapter.OnItemClickListener,
        ComposeTweetDialogFragment.OnComposeDialogFragmentListener {

    @Bind(R.id.recycler_timeline) RecyclerView mTweetsView;
    @Bind(R.id.fab_compose_tweet) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Tweet> mTweets;
    private TweetsAdapter mTweetsAdapter;
    private long mOldestTweetId;
    private long mNewestTweetId;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO - make this an actual singleton instead of hanging a global
        // off of the application. Code smell.
        mTweets = new ArrayList<>();
        mOldestTweetId = 0;
        mNewestTweetId = 0;

        mFloatingActionButton.setOnClickListener(this);

        setupSwipeToRefresh();
        setupTweetListView();
        fetchTweetsForTimeline();
        fetchCurrentUser();
    }

    @Override
    public void onClick(View v) {
        mTweets.get(0).saveTweet();
        //composeNewTweet();
    }

    @Override
    public void onPostedTweet(Tweet newTweetPost) {

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = TweetDetailsActivity.getStartIntent(this, mTweets.get(position));
        startActivity(intent);
    }

    /**
     * Open a compose tweet dialog fragment to allow the user to compose a new tweet
     */
    private void composeNewTweet() {
        ComposeTweetDialogFragment tweetDialogFragment = ComposeTweetDialogFragment.newInstance(mCurrentUser);
        tweetDialogFragment.show(getSupportFragmentManager(), "fragment_compose_tweet_dialog");
    }

    private void setupTweetListView() {
        mTweetsAdapter = new TweetsAdapter(mTweets);
        mTweetsView.setAdapter(mTweetsAdapter);
        mTweetsAdapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTweetsView.setLayoutManager(layoutManager);
        mTweetsView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchTweetsForTimeline();
            }
        });
    }

    private void setupSwipeToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TwitterManager.getInstance().refreshTweetsForTimeline(mNewestTweetId, new TwitterManager.OnTimelineTweetsReceivedListener() {
                    @Override
                    public void onTweetsReceived(List<Tweet> tweets) {
                        if (tweets.size() > 0) {
                            mTweets.addAll(0, tweets);
                            mTweetsAdapter.notifyItemRangeInserted(0, tweets.size());
                            mNewestTweetId = tweets.get(0).getId();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onTweetsFailed(int statusCode, Throwable throwable) {
                        Log.d("DEBUG", "failed to get a response from twitter", throwable);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    /**
     * Fetch the next batch of tweets older than mOldestTweetId.
     */
    private void fetchTweetsForTimeline() {
        TwitterManager.getInstance().fetchTweetsForTimeline(mOldestTweetId, new TwitterManager.OnTimelineTweetsReceivedListener() {
            @Override
            public void onTweetsReceived(List<Tweet> tweets) {
                if (tweets.size() > 0) {
                    mTweets.addAll(tweets);
                    mTweetsAdapter.notifyItemRangeInserted(mTweetsAdapter.getItemCount(), tweets.size());
                    mOldestTweetId = tweets.get(tweets.size() - 1).getServerId();
                    if (mNewestTweetId == 0) {
                        mNewestTweetId = tweets.get(0).getServerId();
                    }
                }
            }

            @Override
            public void onTweetsFailed(int statusCode, Throwable throwable) {
                Log.d("DEBUG", "failed to get a response from twitter", throwable);
            }
        });
    }

    private void fetchCurrentUser() {
        TwitterManager.getInstance().fetchCurrentUser(new TwitterManager.OnCurrentUserReceivedListener() {
            @Override
            public void onUserReceived(User user) {
                mCurrentUser = user;
            }

            @Override
            public void onUserFailed(int statusCode, Throwable throwable) {
                Log.d("DEBUG", "failed to get the current user", throwable);
            }
        });
    }

    private void populateTimeline() {

        String cachedResponse = "[\n" +
                "  {\n" +
                "    \"coordinates\": null,\n" +
                "    \"truncated\": false,\n" +
                "    \"created_at\": \"Tue Aug 28 21:16:23 +0000 2012\",\n" +
                "    \"favorited\": false,\n" +
                "    \"id_str\": \"240558470661799936\",\n" +
                "    \"in_reply_to_user_id_str\": null,\n" +
                "    \"entities\": {\n" +
                "      \"urls\": [\n" +
                " \n" +
                "      ],\n" +
                "      \"hashtags\": [\n" +
                " \n" +
                "      ],\n" +
                "      \"user_mentions\": [\n" +
                " \n" +
                "      ]\n" +
                "    },\n" +
                "    \"text\": \"just another test\",\n" +
                "    \"contributors\": null,\n" +
                "    \"id\": 240558470661799936,\n" +
                "    \"retweet_count\": 0,\n" +
                "    \"in_reply_to_status_id_str\": null,\n" +
                "    \"geo\": null,\n" +
                "    \"retweeted\": false,\n" +
                "    \"in_reply_to_user_id\": null,\n" +
                "    \"place\": null,\n" +
                "    \"source\": \"l\",\n" +
                "    \"user\": {\n" +
                "      \"name\": \"OAuth Dancer\",\n" +
                "      \"profile_sidebar_fill_color\": \"DDEEF6\",\n" +
                "      \"profile_background_tile\": true,\n" +
                "      \"profile_sidebar_border_color\": \"C0DEED\",\n" +
                "      \"profile_image_url\": \"http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg\",\n" +
                "      \"created_at\": \"Wed Mar 03 19:37:35 +0000 2010\",\n" +
                "      \"location\": \"San Francisco, CA\",\n" +
                "      \"follow_request_sent\": false,\n" +
                "      \"id_str\": \"119476949\",\n" +
                "      \"is_translator\": false,\n" +
                "      \"profile_link_color\": \"0084B4\",\n" +
                "      \"entities\": {\n" +
                "        \"url\": {\n" +
                "          \"urls\": [\n" +
                "            {\n" +
                "              \"expanded_url\": null,\n" +
                "              \"url\": \"http://bit.ly/oauth-dancer\",\n" +
                "              \"indices\": [\n" +
                "                0,\n" +
                "                26\n" +
                "              ],\n" +
                "              \"display_url\": null\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"description\": null\n" +
                "      },\n" +
                "      \"default_profile\": false,\n" +
                "      \"url\": \"http://bit.ly/oauth-dancer\",\n" +
                "      \"contributors_enabled\": false,\n" +
                "      \"favourites_count\": 7,\n" +
                "      \"utc_offset\": null,\n" +
                "      \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg\",\n" +
                "      \"id\": 119476949,\n" +
                "      \"listed_count\": 1,\n" +
                "      \"profile_use_background_image\": true,\n" +
                "      \"profile_text_color\": \"333333\",\n" +
                "      \"followers_count\": 28,\n" +
                "      \"lang\": \"en\",\n" +
                "      \"protected\": false,\n" +
                "      \"geo_enabled\": true,\n" +
                "      \"notifications\": false,\n" +
                "      \"description\": \"\",\n" +
                "      \"profile_background_color\": \"C0DEED\",\n" +
                "      \"verified\": false,\n" +
                "      \"time_zone\": null,\n" +
                "      \"profile_background_image_url_https\": \"https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png\",\n" +
                "      \"statuses_count\": 166,\n" +
                "      \"profile_background_image_url\": \"http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png\",\n" +
                "      \"default_profile_image\": false,\n" +
                "      \"friends_count\": 14,\n" +
                "      \"following\": false,\n" +
                "      \"show_all_inline_media\": false,\n" +
                "      \"screen_name\": \"oauth_dancer\"\n" +
                "    },\n" +
                "    \"in_reply_to_screen_name\": null,\n" +
                "    \"in_reply_to_status_id\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"coordinates\": {\n" +
                "      \"coordinates\": [\n" +
                "        -122.25831,\n" +
                "        37.871609\n" +
                "      ],\n" +
                "      \"type\": \"Point\"\n" +
                "    },\n" +
                "    \"truncated\": false,\n" +
                "    \"created_at\": \"Tue Aug 28 21:08:15 +0000 2012\",\n" +
                "    \"favorited\": false,\n" +
                "    \"id_str\": \"240556426106372096\",\n" +
                "    \"in_reply_to_user_id_str\": null,\n" +
                "    \"entities\": {\n" +
                "      \"urls\": [\n" +
                "        {\n" +
                "          \"expanded_url\": \"http://blogs.ischool.berkeley.edu/i290-abdt-s12/\",\n" +
                "          \"url\": \"http://t.co/bfj7zkDJ\",\n" +
                "          \"indices\": [\n" +
                "            79,\n" +
                "            99\n" +
                "          ],\n" +
                "          \"display_url\": \"blogs.ischool.berkeley.edu/i290-abdt-s12/\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"hashtags\": [\n" +
                " \n" +
                "      ],\n" +
                "      \"user_mentions\": [\n" +
                "        {\n" +
                "          \"name\": \"Cal\",\n" +
                "          \"id_str\": \"17445752\",\n" +
                "          \"id\": 17445752,\n" +
                "          \"indices\": [\n" +
                "            60,\n" +
                "            64\n" +
                "          ],\n" +
                "          \"screen_name\": \"Cal\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Othman Laraki\",\n" +
                "          \"id_str\": \"20495814\",\n" +
                "          \"id\": 20495814,\n" +
                "          \"indices\": [\n" +
                "            70,\n" +
                "            77\n" +
                "          ],\n" +
                "          \"screen_name\": \"othman\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"text\": \"lecturing at the \\\"analyzing big data with twitter\\\" class at @cal with @othman  http://t.co/bfj7zkDJ\",\n" +
                "    \"contributors\": null,\n" +
                "    \"id\": 240556426106372096,\n" +
                "    \"retweet_count\": 3,\n" +
                "    \"in_reply_to_status_id_str\": null,\n" +
                "    \"geo\": {\n" +
                "      \"coordinates\": [\n" +
                "        37.871609,\n" +
                "        -122.25831\n" +
                "      ],\n" +
                "      \"type\": \"Point\"\n" +
                "    },\n" +
                "    \"retweeted\": false,\n" +
                "    \"possibly_sensitive\": false,\n" +
                "    \"in_reply_to_user_id\": null,\n" +
                "    \"place\": {\n" +
                "      \"name\": \"Berkeley\",\n" +
                "      \"country_code\": \"US\",\n" +
                "      \"country\": \"United States\",\n" +
                "      \"attributes\": {\n" +
                "      },\n" +
                "      \"url\": \"http://api.twitter.com/1/geo/id/5ef5b7f391e30aff.json\",\n" +
                "      \"id\": \"5ef5b7f391e30aff\",\n" +
                "      \"bounding_box\": {\n" +
                "        \"coordinates\": [\n" +
                "          [\n" +
                "            [\n" +
                "              -122.367781,\n" +
                "              37.835727\n" +
                "            ],\n" +
                "            [\n" +
                "              -122.234185,\n" +
                "              37.835727\n" +
                "            ],\n" +
                "            [\n" +
                "              -122.234185,\n" +
                "              37.905824\n" +
                "            ],\n" +
                "            [\n" +
                "              -122.367781,\n" +
                "              37.905824\n" +
                "            ]\n" +
                "          ]\n" +
                "        ],\n" +
                "        \"type\": \"Polygon\"\n" +
                "      },\n" +
                "      \"full_name\": \"Berkeley, CA\",\n" +
                "      \"place_type\": \"city\"\n" +
                "    },\n" +
                "    \"source\": \"s\",\n" +
                "    \"user\": {\n" +
                "      \"name\": \"Raffi Krikorian\",\n" +
                "      \"profile_sidebar_fill_color\": \"DDEEF6\",\n" +
                "      \"profile_background_tile\": false,\n" +
                "      \"profile_sidebar_border_color\": \"C0DEED\",\n" +
                "      \"profile_image_url\": \"http://a0.twimg.com/profile_images/1270234259/raffi-headshot-casual_normal.png\",\n" +
                "      \"created_at\": \"Sun Aug 19 14:24:06 +0000 2007\",\n" +
                "      \"location\": \"San Francisco, California\",\n" +
                "      \"follow_request_sent\": false,\n" +
                "      \"id_str\": \"8285392\",\n" +
                "      \"is_translator\": false,\n" +
                "      \"profile_link_color\": \"0084B4\",\n" +
                "      \"entities\": {\n" +
                "        \"url\": {\n" +
                "          \"urls\": [\n" +
                "            {\n" +
                "              \"expanded_url\": \"http://about.me/raffi.krikorian\",\n" +
                "              \"url\": \"http://t.co/eNmnM6q\",\n" +
                "              \"indices\": [\n" +
                "                0,\n" +
                "                19\n" +
                "              ],\n" +
                "              \"display_url\": \"about.me/raffi.krikorian\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"urls\": [\n" +
                " \n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"default_profile\": true,\n" +
                "      \"url\": \"http://t.co/eNmnM6q\",\n" +
                "      \"contributors_enabled\": false,\n" +
                "      \"favourites_count\": 724,\n" +
                "      \"utc_offset\": -28800,\n" +
                "      \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/1270234259/raffi-headshot-casual_normal.png\",\n" +
                "      \"id\": 8285392,\n" +
                "      \"listed_count\": 619,\n" +
                "      \"profile_use_background_image\": true,\n" +
                "      \"profile_text_color\": \"333333\",\n" +
                "      \"followers_count\": 18752,\n" +
                "      \"lang\": \"en\",\n" +
                "      \"protected\": false,\n" +
                "      \"geo_enabled\": true,\n" +
                "      \"notifications\": false,\n" +
                "      \"description\": \"Director of @twittereng's Platform Services. I break things.\",\n" +
                "      \"profile_background_color\": \"C0DEED\",\n" +
                "      \"verified\": false,\n" +
                "      \"time_zone\": \"Pacific Time (US & Canada)\",\n" +
                "      \"profile_background_image_url_https\": \"https://si0.twimg.com/images/themes/theme1/bg.png\",\n" +
                "      \"statuses_count\": 5007,\n" +
                "      \"profile_background_image_url\": \"http://a0.twimg.com/images/themes/theme1/bg.png\",\n" +
                "      \"default_profile_image\": false,\n" +
                "      \"friends_count\": 701,\n" +
                "      \"following\": true,\n" +
                "      \"show_all_inline_media\": true,\n" +
                "      \"screen_name\": \"raffi\"\n" +
                "    },\n" +
                "    \"in_reply_to_screen_name\": null,\n" +
                "    \"in_reply_to_status_id\": null\n" +
                "  },\n" +
                "  {\n" +
                "    \"coordinates\": null,\n" +
                "    \"truncated\": false,\n" +
                "    \"created_at\": \"Tue Aug 28 19:59:34 +0000 2012\",\n" +
                "    \"favorited\": false,\n" +
                "    \"id_str\": \"240539141056638977\",\n" +
                "    \"in_reply_to_user_id_str\": null,\n" +
                "    \"entities\": {\n" +
                "      \"urls\": [\n" +
                " \n" +
                "      ],\n" +
                "      \"hashtags\": [\n" +
                " \n" +
                "      ],\n" +
                "      \"user_mentions\": [\n" +
                " \n" +
                "      ]\n" +
                "    },\n" +
                "    \"text\": \"You'd be right more often if you thought you were wrong.\",\n" +
                "    \"contributors\": null,\n" +
                "    \"id\": 240539141056638977,\n" +
                "    \"retweet_count\": 1,\n" +
                "    \"in_reply_to_status_id_str\": null,\n" +
                "    \"geo\": null,\n" +
                "    \"retweeted\": false,\n" +
                "    \"in_reply_to_user_id\": null,\n" +
                "    \"place\": null,\n" +
                "    \"source\": \"web\",\n" +
                "    \"user\": {\n" +
                "      \"name\": \"Taylor Singletary\",\n" +
                "      \"profile_sidebar_fill_color\": \"FBFBFB\",\n" +
                "      \"profile_background_tile\": true,\n" +
                "      \"profile_sidebar_border_color\": \"000000\",\n" +
                "      \"profile_image_url\": \"http://a0.twimg.com/profile_images/2546730059/f6a8zq58mg1hn0ha8vie_normal.jpeg\",\n" +
                "      \"created_at\": \"Wed Mar 07 22:23:19 +0000 2007\",\n" +
                "      \"location\": \"San Francisco, CA\",\n" +
                "      \"follow_request_sent\": false,\n" +
                "      \"id_str\": \"819797\",\n" +
                "      \"is_translator\": false,\n" +
                "      \"profile_link_color\": \"c71818\",\n" +
                "      \"entities\": {\n" +
                "        \"url\": {\n" +
                "          \"urls\": [\n" +
                "            {\n" +
                "              \"expanded_url\": \"http://www.rebelmouse.com/episod/\",\n" +
                "              \"url\": \"http://t.co/Lxw7upbN\",\n" +
                "              \"indices\": [\n" +
                "                0,\n" +
                "                20\n" +
                "              ],\n" +
                "              \"display_url\": \"rebelmouse.com/episod/\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"urls\": [\n" +
                " \n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"default_profile\": false,\n" +
                "      \"url\": \"http://t.co/Lxw7upbN\",\n" +
                "      \"contributors_enabled\": false,\n" +
                "      \"favourites_count\": 15990,\n" +
                "      \"utc_offset\": -28800,\n" +
                "      \"profile_image_url_https\": \"https://si0.twimg.com/profile_images/2546730059/f6a8zq58mg1hn0ha8vie_normal.jpeg\",\n" +
                "      \"id\": 819797,\n" +
                "      \"listed_count\": 340,\n" +
                "      \"profile_use_background_image\": true,\n" +
                "      \"profile_text_color\": \"D20909\",\n" +
                "      \"followers_count\": 7126,\n" +
                "      \"lang\": \"en\",\n" +
                "      \"protected\": false,\n" +
                "      \"geo_enabled\": true,\n" +
                "      \"notifications\": false,\n" +
                "      \"description\": \"Reality Technician, Twitter API team, synthesizer enthusiast; a most excellent adventure in timelines. I know it's hard to believe in something you can't see.\",\n" +
                "      \"profile_background_color\": \"000000\",\n" +
                "      \"verified\": false,\n" +
                "      \"time_zone\": \"Pacific Time (US & Canada)\",\n" +
                "      \"profile_background_image_url_https\": \"https://si0.twimg.com/profile_background_images/643655842/hzfv12wini4q60zzrthg.png\",\n" +
                "      \"statuses_count\": 18076,\n" +
                "      \"profile_background_image_url\": \"http://a0.twimg.com/profile_background_images/643655842/hzfv12wini4q60zzrthg.png\",\n" +
                "      \"default_profile_image\": false,\n" +
                "      \"friends_count\": 5444,\n" +
                "      \"following\": true,\n" +
                "      \"show_all_inline_media\": true,\n" +
                "      \"screen_name\": \"episod\"\n" +
                "    },\n" +
                "    \"in_reply_to_screen_name\": null,\n" +
                "    \"in_reply_to_status_id\": null\n" +
                "  }\n" +
                "]";
    }

}
