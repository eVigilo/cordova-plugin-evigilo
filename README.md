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

#### initObj.push

Attribute | Type | Default | Description
--------- | ---- | ------- | -----------
`android.smallIcon` | `string` | | Optional. The name of a drawable resource to use as the small-icon. The name should not include the extension.
`android.iconColor` | `string` | | Optional. Sets the background color of the small icon on Android 5.0 and greater. [Supported Formats](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String))
`android.largeIcon` | `string` | | Optional. The name of a drawable resource to use as the large-icon and will be shown inthe alert popup. The name should not include the extension.


### Evigilo.sendEvent(eventObj, success, failure)

Report of an emergency event which will be recieved and handled in the evigilo clous.

Parameter | Type | Default | Description
--------- | ---- | ------- | -----------
`eventObj` | `Object` | `{}` | An object describing representing the event which we want to activate on the server.
`success` | `function` | | a success callback for the plugin in case the event was succesfully dispatched.
`failure` | `function` | | a failure callback for the plugin in case of native side error.


#### eventObj

Attribute | Type | Default | Description
--------- | ---- | ------- | -----------
`android.smallIcon` | `string` | | Optional. The name of a drawable resource to use as the small-icon. The name should not include the extension.
`android.iconColor` | `string` | | Optional. Sets the background color of the small icon on Android 5.0 and greater. [Supported Formats](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String))
`android.largeIcon` | `string` | | Optional. The name of a drawable resource to use as the large-icon and will be shown inthe alert popup. The name should not include the extension.


##### Example

```javascript
if (Evigilo && Evigilo.sendEvent) {
          var eventObj = {};
          eventObj.phone = '11111111';
          eventObj.name = 'evigilo smart';
          eventObj.message = 'I am in trouble';
          eventObj.photoPath = panicData.imageUrl;//comming from camera plugin
          eventObj.soundPath = panicData.soundPath; // coming from media plugin
          eventObj.reportType = 1; // 1 - for panic , 2 - for observation
          eventObj.event = 11; // the event type id
          eventObj.subEvent = 11; // the sub event type id
          eventObj.messageId = 2; // a push message id to link with the emergency event whicj is reported

          Evigilo.sendEvent(eventObj
          ,defer.resolve, defer.reject);
        }
```
