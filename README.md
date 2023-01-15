# News App
News App is a simple MVVM Clean Architecture project to showcase sample usages of Room, Retrofit, Kotlin Coroutines, and Hilt. There are also a few unit tests created. It uses [News API](https://newsapi.org/) to display breaking news and allows you to save them for later.

## Installation
1. Clone the repository `git clone https://github.com/michalgwo/news-app.git`

2. Go to [News API website](https://newsapi.org/) and generete your free API key

3. Add `API_KEY` variable to your `gradle.properties` file, in case you don't have the file, you need to create it. `API_KEY="YOUR API KEY"`

## Features
- Get news headlines from API and display them in RecyclerView
- Read whole articles by clicking on them
- Save articles
- Pagination
- Search news
- Get saved news headlines and display in RecyclerView
- Delete saved artices by swipe
- Retrieve deleted article by clicking "Undo" on snackbar
