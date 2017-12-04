# cordova-plugin-evigilo
> The plugin gives the developer an API to use evigilo services such as : Create emergency events in evigilo's cloud system. Get content which was publish in evigilo cloud. Register to emergency push notifications submited from the evigilo cloud. and more

## Installation
To install from the command line you need to also add the customer unique instance name give by eVigilo:

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName 
```
###On premise installation
If the plugin needs to communicate with an on premise server with custom url 
then use : 

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName --variable INIT_URL=initUrl
```
and if you also have another server site : 

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName --variable INIT_URL=initUrl --variable DR_INIT_URL=drInitUrl
