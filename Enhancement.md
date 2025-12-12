# Notes: When moving or changing or whatever, do not leave any ghosting like files, function, ect. Always remove them if they are not needed anymore, that also include UIs or whatever.


## QR:
1. Need some other functions such as converting image into QR or reverse it back if possible.
2. QRcode recognition like face recognition, it can tell which QRs are the same.
3. When converting the image to QR or reverse, all their information must remain.

## AI
* Right now we only have the AI suggest the filenames

1. Human face recognition.
2. Images recognition, normally face recognition only focus on human face not other thing, and image recognition need to focus on other thing like "QR", "Wallpaper", "Fiction characters", ect, ect.
3. Implement the AI feature into the template creating as well, like generating a preset based on the user description (We gonna need a place for user to type their desired need). This feature need to have some countermeasure for when user asking for something that is not about the "Template".

### Remove:
1. Remove account and Cloud Sync from main screen


### Move:
1. Group activity log and history together and move them to a Navigation Drawer
2. Move both Share QR and Scan QR to a new UI which just called QR Functions
3. Move the AI suggestions to batch renaming. After done with the problem with batch renaming ofc.

### Problems:
1. The Batch renaming is currently not what we desired. We need something like "Batch_rename_template.png" but with some exceptions, we want: The destination folder function, the preserve original file order, the cancel function, the start function, the clear files function, all the numbering function, the file type function(it has many different format to convert to when renaming), the gallery function that only show images to choose, the select folder(this function allow the app to take all the files in a single folder).

2. We need another theme function that take custom image not just the color. When the image is uploaded, the app will just display the image in the background globally, but there is should be another button that user can click then the app will open another screen to let user change the image background position, coloring(similar to samsung device that let user change to desirable color), blur with slider to adjust the level of blur.

3. Move the image-based theme to the theme mode, while also change the theme mode at appearance section to have both Image-based theme and custom image theme.

4. Back button is missing in folder monitoring

5. We have cloud sync issues with how the connect function works, when user clicked connect on one of the option, every other connect buttons also started the circle connecting animation.

5. Out of all the 3 cloud connection function, we only need 2 which are google drive and onedrive. Google drive is connected automatically when user login with google account and the same for onedrive with microsoft.

6. The auto backup function should be turned on from the moment the app first started for user or else there is no way for user to know if their files will be safe when the renaming operating, which leading to losing the original file. We could have an option(I'm talking about the having it at every part of the function that make change to files) to turn on/off auto deleting the old files when done changing any aspect them(The auto delete function should be remembered the next time user went to use any file changing function. I think when the auto delete turned on, the auto backup should turn off, since it might just conflict with each other).