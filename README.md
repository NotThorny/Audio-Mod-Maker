# Hoyo Audio Mod Maker (HAMM)

Hoyo Audio Mod Maker (HAMM) is a tool for quickly and easily making audio mods for hoyo games. Currently only supporting Genshin Impact.
Mods can be saved separately to be used/shared, and can be immediately automatically applied to the game. The idea is to make it as simple as possible for anyone to make audio mods without needing any understanding or tedium.

The option to mute lines comes by default in the dropdown for selecting a replacement. You can click the "Add Sounds" button and then close the popup without adding any if you only wish to mute lines.

Mods stack - if your audio is already modded when you apply a new one through the app, the new one will be added on top of the old one (sounds for the same voice lines will be overwritten).

## Installation
**<ins>Installer:</ins>** 
- Get latest Audio-Mod-Maker_Installer.exe release from [releases](https://github.com/NotThorny/Audio-Mod-Maker/releases).
- Run the installer and go through to the end.
- HAMM is now installed on your PC

![javaw_eEqHkHuyFU](https://github.com/user-attachments/assets/dc75b5d6-7bed-4dd3-9808-fdbb36f8698a)

## Setup & Usage
Steps 2 and 3 are done only for first time opening the app.

**1.** Open Audio Mod Maker.

**2.** You will be prompted to download WWise. Click OK. This is **required** to convert audio for the game. The download and setup will happen in the background, so you can forget about it and continue.

**3.** Select your game executable and in-game voice over language.

**4.** In the top left, select "**Add Sounds**"

**5.** Select your choice of audio files, as many as you want to add to the game.

**6.** In the center, pick a character and the voice line you want to replace.

**7.** A dropdown will appear on the voice line you choose, select it and pick the sound.

**8.** Repeat steps 6-7 until you have selected all the replacements you want

**9.** Click "**Make Mod**" in the bottom right. If you have not made a backup already, you will be prompted to do so.

**10.** If you have selected to save separately, choose a name in the popup.

**11.** Your mod is now created. If you have selected to auto apply it to game, it is already applied to your game.

You can find all mods you have created in your mods folder, access it by going to the **Top left -> File -> Open mods folder**.
Any replacements not saved separately will be in the "all" folder.

## Applying Mods from Elsewhere
To apply a mod someone else has made, or one you have made and have in your mods folder do the following:

 1. Open HAMM
 2. At the top left, select "File" then "Open mods folder"
 3. Put your mod folder (extracting if it is a zip) into there
 4. In HAMM, at the top left select "Mod Management"
 5. Select "Apply Mod From Folder to Game", then select the name of your mod

 The selected mod will be added to the game.

### For going back to vanilla audio
Your backup audio can be restored to your game from the top left **File** -> **Restore backup**
Alternatively, you can manually copy the files over.

## Issues & PRs
- Please use the issues page to report any issues, PRs are welcomed.
 
## **Building from source (For advanced users):**

Building from source is for if you wish to build the application from source code yourself. If you are a normal user, please follow the installer instructions at the top.
- Run these commands:
```
git clone https://github.com/NotThorny/Audio-Mod-Maker.git
cd Audio-Mod-Maker 
mvn package
```
You can find the result in the target folder.

## Credits:
- [AI-Hobbyist](https://github.com/AI-Hobbyist): Voice indexes
- [DiXiao](https://gamebanana.com/members/2182818): Original audio modding tutorial and information
- [Brainless Hero](https://gamebanana.com/members/2413509): Sorting scripts for indexes
- [FFmpeg](https://github.com/FFmpeg/FFmpeg): Audio converting
- [WWise](https://www.audiokinetic.com/en/wwise/overview/): Audio converting
- [OpenJDK](https://openjdk.org/) - Java runtime
