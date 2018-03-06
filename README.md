# Android Colour Extraction and Video Player Application

During my internship in the summer of 2017, my job was to build an Android app that could:
  - Extract colour from the frame of a playing video
  - Use Philips HUE lighting API to create ambient lighting
  - Stream the video to a media server
  
The purpose of the app was to test the viability of Philips HUE lighting system in the Indian market. To that end, my job was to build an app that would extract the most dominant colour as well as some vibrant and muted colours from the frames of a video. The app that I built works on Android of API 14 and above and it basically finds a video in the 'Downloads' folder, plays the video, extracts said colours twice a second and, using the API, creates ambient lighting in a room with the Philips HUE lights. The video would also simultaneously, stream to a media server so that the video/movie can be watched on a larger screen (TV or monitor) instead of your phone.

As some of my work was covered under an NDA, the Video-Player app in this Github only shows the functionality of the colour extraction portion of my internship.
