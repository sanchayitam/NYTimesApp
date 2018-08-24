# NYTimesApp
A simple Android app which will display a list or grid of news articles retrieved from the NY Times
search API.

Requirements:
1. The app should have 2 screens defined below.

Home Screen:
- Allows the user to enter a search term and displays the results in either a list or grid view.
- Use the ActionBar SearchView or custom layout as the query box instead of an EditText.
- Results should display:
- Article thumbnail
- Article headline
- Selecting an item in search results should open the detail screen.
- Should have pagination of results

Detail Screen:
- Displays a detailed view of article.
- User can share a link to their friends or email it to themselves

The following optional features are implemented:

 Implements robust error handling, check if internet is available, handle error cases, network failures
 Used the ActionBar SearchView or custom layout as the query box instead of an EditText
 User can share an article link to their friends or email it to themselves
 
 Open Library used:
 -Android Async HTTP - Simple asynchronous HTTP requests with JSON parsing
 -Glide - Image loading and caching library for Android
