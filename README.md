# Fetcher [![Build Status](https://travis-ci.org/lk2-iosr/fetcher.svg?branch=master)](https://travis-ci.org/lk2-iosr/fetcher)

Service for fetching posts with reactions from specified Facebook pages.
Written in Java using Dropwizard.

## Running application locally

### Prerequsits 
 * Installed [Docker](https://docs.docker.com/engine/installation/)
 * [Facebook App Access Token](https://developers.facebook.com/docs/facebook-login/access-tokens/#apptokens)

### Instructions
1. Clone this repository: 
```https://github.com/lk2-iosr/fetcher.git```
2. Run Docker
3. Run 
```mvn clean install```. 
This will build `jar` file and create docker image ``iosr.facebookapp/fetcher``
4. Run 
```docker run --env-file <PATH_TO_FILE_WITH_ENVIRONMENT_VARIABLES> -p <PORT>:5000 iosr.facebookapp/fetcher```

### Environment variables

* `FACEBOOK_URI` (required) - URI of Facebook GraphAPI (https://graph.facebook.com/v2.10/)
* `FACEBOOK_OAUTH_KEY` (required) - Facebook Access Token
* `PAGES` (required) - comma separated list of `<page id>:<page title>` pairs
* `FETCHER_INTERVAL_IN_MINUTES` (optional) - default value is 5
* `POSTS_LIMIT` (optional) - default value is 50, max value is 100 because of Facebook limits

## How it works
In given interval application fetches the latest `N` posts from specified pages and for each of them produces message in format:
```json
{
  "id": "postId",
  "message": "post's text",
  "link": "published link",
  "shares": 1, 
  "likes": 2,
  "comments": 3,
  "createdTime": "2017-12-01T15:04:24Z"
}
```

These messages will be published to Amazon SNS or SQS and then consumed by [stats](https://github.com/lk2-iosr/stats) and [publisher](https://github.com/lk2-iosr/publisher)
