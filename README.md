# cordova-plugin-evigilo
> The plugin gives the developer an API to use evigilo services such as : 
>- Create emergency events in evigilo's cloud system. 
>- Get content which was publish in evigilo cloud. 
>- Register to emergency push notifications submited from the evigilo cloud. and more

## Installation
To install from the command line you need to also add the customer unique instance name give by eVigilo:

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName 
```
### On premise installation
If the plugin needs to communicate with an on premise server with custom url 
then use : 

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName --variable INIT_URL=initUrl
```
and if you also have another server site : 

```
cordova plugin add https://github.com/evigilo/cordova-plugin-evigilo --variable INSTANCE=instanceName --variable INIT_URL=initUrl --variable DR_INIT_URL=drInitUrl
```
## API
### Evigilo.init(initObj, failureCallback)

Initializes the plugin on the native side. and registeres emergency events push notifications.

**Note:** like all plugins you must wait until you receive the [`deviceready`](https://cordova.apache.org/docs/en/5.4.0/cordova/events/events.deviceready.html) event before calling `Evigilo.init()`.

Parameter | Type | Default | Description
--------- | ---- | ------- | -----------
`initObj` | `Object` | `{push:{}}` | An object describing relevant options for the plugin.
`failureCallback` | `function` | `{}` | a failure callback for the plugin in case of native side error.



