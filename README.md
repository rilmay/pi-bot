# pi-bot
Telegram bot, created for use in small home computers and do small home jobs;)

## Current version available commands:

- /register - user registration or allowed users selection (authorization for using pi bot)

- /photo - taking a photo with connected usb cam

- /video - taking a video in GIF format with connected usb cam

## Usage

First you need to register your bot in the official BotFather bot and get your new bot's telegram token. 
It is recommended to change the bot commands to the commands that this application supports (this setting is available in BotFather). 
Next, you need to run the following command:

*java -jar pibot.jar [args]*

Arguments:

*-r --resolver*

Strategy to resolve external variables

Possible values: *cli* , *environment*

Default value: *cli*

Not required

*-d --data* 

Data to resolve external variables when *--resolver* argument is set to *cli*
(if resolver is set to *environment*, then all external properties will be searched in 
environment variables)

Example value: *TELEGRAM_TOKEN=abdfg13435,ALLOWED_USERS=@telegram_username@another_username*

*TELEGRAM_TOKEN* variable is required, *AUTHORIZATION_STRATEGY* variable is set to *usernames* by default
(in that case, *ALLOWED_USERNAMES* variable is required), if *AUTHORIZATION_STRATEGY* is set to *password*, 
then *BOT_PASSWORD* variable is required.

### List of all external variables:

- *TELEGRAM_TOKEN*

- *AUTHORIZATION_STRATEGY*(default *usernames*)

- *MAX_REGISTERED_USERS*(default value *5*)

- *MAX_ATTEMPTS*(max attempts to register, default is *2*)

- *BOT_PASSWORD*(password for registration)

- *ALLOWED_USERNAMES*

- *WEBCAM_NUMBER*(usb video device number used for taking pictures, default is *0*)

- *VIDEO_FRAMES*(GIF video frames count, default is *10*)

- *VIDEO_SPEED*(factor by which the video will be accelerated, default is *1.8*)