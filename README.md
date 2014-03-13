storybook
=========

An app to make children's story books

I decided to try to make a storybook app.  
I wanted to shy away from fragments for this app and create overly obfuscated and complex adapters to show each page.  
This may have been a mistake.  

This app was to learn how to marshall data with JSON and parceable.  
I used this experience to learn how to save and load large projects to local storage.
I had to extend some views in order to accomplish the textViews and ImageViews I wanted.
There is some creative/interesting/really bad use of service and broadcast receivers. 

Bugs:
-Cover and ToC do not save Text<br>
-Sometimes Save As crashes App<br>
-Load from menu does not work correctly<br>
-Keypad covers the textView as you type<br>
<br>
I have not completed this app yet, it will be completed in the future.
<br>
Future features:<br>
-Fix Cover and ToC not saving text.<br>
-Switch to fragments for the storypages.<br>
-Have the table of contents be a listView that navigates to the proper page.<br>
-Scale loaded images.<br>
-Allow user to launch the whiteboard app and return a drawing.<br>
-Rewrite the service/broadcast reciever stuff I made and replace with listeners/observers/callbacks, <br>
  anything but what I have.<br>
-Implement bold,italic,underline from 'A' menu<br>
-Maybe store all stories on a parse server and be able to retireve them<br>
