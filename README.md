# ImagePicker

Technology Used- Jetpack components like Data binding, View model, Live Data

Architecture - MVVM

Other libraries- Glide, UCrop, Firebase real time DB


Supports API 21-29, 4 to 6.5 inch devices roughly(300 dpi toless than 600 dpi) and portrait and landscape mode

UI flow-

1.) Home Screen- Image Gallery with GridLayout of size 2

2.) Full sized image on clicking a thumbnail.

3.) When back button is pressed shared element animation back to Home screen.

4.) Fab on Home screen with option to upload image from camera or gallery

5.) After image is taken from camera or selected from gallery, it can be cropped/zoomed in or out using UCrop

6.) Image is uploaded on Firebase Db and automatically updated on home screen as real time Db is used.

Setup Instructions-

1.) Download and unzip the source code (zip file) from github.

2.) Open it in Android studio 3.5.1 or higher with latest SDK (API 29 support)

3.) Sync the gradle files and build the project

4.) Run the project on any emulator configuration with API 21 or higher
