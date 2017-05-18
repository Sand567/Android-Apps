# Android-Apps
Android Applications

Temperature Converter - A simple app to convert temperature based on the radio button checked. Either Celsius to Fahrenheit or Fahrenheit to Celsius

Notepad - A single note application used to store data such as text typed in to EditText. Works well in both Portrait and Landscape Orientation.

Multi Note - This is an application which has the ability to store multiple notes typed in by the user and uses Async tasks to save and load data.

Stock Watch - This is a real world android application which uses internet and makes API calls to download Stock data and uses SQLite database to store data for that particular stock and loaded back when the application gets loaded.

Know Your Government - Uses Location and Internet Services to determine the current location and makes API calls for that particular location retrieves data and displays it in the Recycler View.

News Gateway - Is a News app which gives us dyncamic News based on the API call. Async Tasks were used to retrieve data and and populate the options menu and drawer layout dynamically. And used fragments and view pager to scroll between different news fragments. 

Features used in the applications built

1. Constraint Layout, Bundles
2. Intents, Files 
3. Async Tasks, Recycler View, Card View
4. Internet, SQLite
5. Locations, Implicit and Explicit Intents
6. Drawer layout, View Pager
7. Fragments and Services
8. Broadcast Services

Constraint Layout is a easy to use layout which prompts the user to make automatic associations to build a clean View for the data. This was used a replacement for Liner Layout and Relative Layout

Bundles are a simple and easy way to store data and retrieve data. This can be used while the orientation changes. Or used to save data in fragments

Async Tasks are those which are invoked by the Main activity and run parallel to Main activity. Operations can be done in async tasks where the main activity doesn't need any involvement and the async tasks perform operations on its own and display the result. Examples like downloading data, downloading an image, playing music.

Recycler View was used as replacement of the traditional List Views which has its own implementation of onClick and onLongClick methods. Card View gives the recycler view more a styled effect. Which gives more style to each layout in Recycler View.

Internet was used to download data in the form of JSON by making API calls

SQLite is a lighter and faster version which is more compatible with android mobile applications which can be used to store, add, update and delete data.

Locations Services were used to determine the current location and display location specific data.

Implicit events were used to navigate to websites by parsing the URL obtained by the data we downloaded in Async Tasks. Linkify used to create links for addresses, phone numbers, email address and website address. by clicking on it will take us to Phonebook, Email app.

Drawer Layout is used in every modern android applications which acts similary to a Option Menu. 

View Pager is way to scroll different fragments which doesn't create a new activity for every fragment instead it creates a container once the fragment arrives and removed once not in use but is put back once the fragment is instantiated.

Fragments are also like an Activity but here many activities can be placed in a fragment. Dyncamically change content when data arrives

Services are activities which once started will not be stopped automatically but it is responsibility of programmer to shutdown the service.  

Broadcasts are those which can be heard by every listener. So in order to listen to broadcasts a receiver is to be registered to send and receive messages.
