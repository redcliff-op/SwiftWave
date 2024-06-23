**SwiftWave**

Crafting Connections, Crafting Conversations: Enter the Realm of SwiftWave!

A Fully Functional Online Social Media Application Built on MVVM architecture using Kotlin + Jetpack Compose

**Screenshots**
<img width="2100" alt="1" src="https://github.com/redcliff-op/SwiftWave/assets/78088434/1cf55d17-060c-4d10-8445-13e00cc84b71">
<img width="2339" alt="knu" src="https://github.com/redcliff-op/SwiftWave/assets/78088434/26fa7dff-cb66-4d74-92ed-31b0578099da">
<img width="5061" alt="Untitled (2)" src="https://github.com/redcliff-op/SwiftWave/assets/78088434/1cebb3fa-659a-40cb-9c1d-f646734d16f4">
<img width="8351" alt="Untitled (3)" src="https://github.com/redcliff-op/SwiftWave/assets/78088434/fbcb38b1-22ae-45ef-9853-08bf34ef6b5f">

**Features**

**1) Messages and Media**

- Send / Recieve Images, VIdeos of Files (Any file ie Apks, Documents, Audio etc)
- Send / Recieve Messages with / without Attached Media
- Get Realtime Animated Upload Status, ie megabytes uploaded and total megabytes
- Choose Media from Any Source - Camera, Gallery, Google Photos, File manager etc
- Crop, Resize, Rotate, Flip Images
- Choose Upload Quality for Images ( from 10-100% )
- Read Receipts ( Can be enabled/disabled by the user )
- Animated Typing Indicator
- Online Status Indicator
- Search for Messages ( app auto scrolls b/w multiple messages if they match the searched string )
- Swipe Gestures to Reply and Forward
- Reply to Messages, Stories, Images, Videos and Files along with a preview (Clicking on the card Automatically scrolls to the replied Messages)
- Forward Messages and Media (when owner deletes the media, all the forwarded instances of that media become unavailable aswell for security)
- React to Messages using Emojis ( Recently Used Emojis for each user are cloud synced for seamless experience at every login on any device )
- View Bio and Email of the Other person at Chat Screen
- Star Messages
- Edit Messages or Media-Message bundle
- Delete Messages
- Copy Messages
- Open Images (with zoom functionality)
- Play Videos (with loop and speed control)
- Images and Videos are LRU cached locally so they don't redownload again and again
- Open any file with suitable intent
- Floating Button to scroll all the way to the bottom

**2) Stories**

- Upload / View Stories (with zoom)
- Check who viewed your story
- check who liked your story
- reply to stories (Automatically transitions to replied user's chat screen after replying)
- Stories auto delete after 24h

**3) Chat Screen Customisations**

- Change Font Size
- Change Rounded Corner Radius
- Change Chat background's Visibility
- Change Chat background's Material3 Color Tint
- Switch b/w diff chat backgrounds
- Swap Chat Bubble Colors
- **all Customisation preferences for each user are cloud synced for seamless experience at every login on any device**

**4) Account and Settings Screen**

- Change Profile Picture ( choose from any source, strict 1:1 aspect ratio for profile pictures by default )
- Change Bio
- Block/Unblock Users
- Add/Remove Users from favorites list
- Enable/Disable Read Receipts
- Adjust Upload Quality For Images and Stories (b/w 10-100%)
- Delete contact ( also deletes all the chats at once )

**5) Notifications**

- Recieve Notifications for
- Messages
- Media and Files
- When someone likes your story

**6) Other Main Screen Features**
- Show Latest message for each user (along with time and icons for media and files if any)
- Swipe Gestures to Favorite, Delete or Block a User
- Search For Users
- View Stories
- Add New Users

**7) Other Exciting Features**
- Google Sign in Authentication
- Material3 Color Theming - App Themes itself according to the dominant color of the Wallpaper
- Offline Data Persistence - If the Device is offline, the app operates normally on cached data, when Data Access is available, app Syncs all the locally cached Changes to cloud again
- Loaded with Animations for each operation to give the best possible user experience

**Underhood Implementations-**

- Firebase Authentication ( Google )
- Cloud FireStore NoSQL Database to Store User Data and Messages
- Firebase Storage to store Images, Videos and Files
- FCM and OkHTTP For Realtime Notifications
- Coil and Glide Library for Image Loading and Caching
- Media3 ExoPlayer for Video Playback
- Lottiefiles and Compose Animations
- Dynamic Material3 ColorScheme
  
Thanks to @dsa28s for his implementation of Media3 ExoPlayer

Thanks to @CanHub for Image Cropper

**Click the Link to see the App in Action!**
https://drive.google.com/file/d/1XrQtiqISQwlzP7vEYHODuo7poUjuY_XJ/view?usp=sharing


