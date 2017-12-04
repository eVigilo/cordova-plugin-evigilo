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
### installation for On premise server sites
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

Report of an emergency event which will be recieved and handled in the evigilo cloud.

Parameter | Type | Default | Description
--------- | ---- | ------- | -----------
`eventObj` | `Object` | `{}` | An object describing representing the event which we want to activate on the server.
`success` | `function` | | a success callback for the plugin in case the event was succesfully dispatched.
`failure` | `function` | | a failure callback for the plugin in case of native side error.


#### eventObj

Attribute | Type | Default | Description
--------- | ---- | ------- | -----------
`phone` | `string` | | Optional. The reporter mobile phone
`name` | `string` | | Optional. The reporter name
`message` | `string` | | Optional. The message text that we want to be sent.
`photoPath` | `string` | | Optional. The photo native path to be sent to the server. usually this comes from the cordova-plugin-camera result.
`soundPath` | `string` | | Optional. The media native path to be sent to the server. usually this comes from the cordova-plugin-media result.
`reportType` | `int` | | required. The severity of the event where : 1 is for panic , 2 is observation
`event` | `int` | | Optional. The event group id which is ussually recieved as a content from the server.
`subEvent` | `int` | | required. The sub type id which is ussually recieved as a content from the server.
`messageId` | `int` | | Optional. a push message id to link with the emergency event whicj is reported.



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



### Evigilo.postSettings(settingObj, success, failure)

Change user account settings on the evigilo cloud.
This function should be called either after a login phase for a non anounimous users. or after the device registration is made which is usually at the beginning, since you need the creation of a new account key in the native code is dependent on this.

Parameter | Type | Default | Description
--------- | ---- | ------- | -----------
`settingObj` | `Object` | `{}` | An object describing representing the settings which we want to activate on the server.
`success` | `function` | | a success callback for the plugin in case the event was succesfully dispatched.
`failure` | `function` | | a failure callback for the plugin in case of native side error.


#### settingObj

Attribute | Type | Default | Description
--------- | ---- | ------- | -----------
`vendors.mobile` | `string` | | Optional. set the account user mobile number which can also be used for SMS dispatching if vendors.isSendSms is set to true
`vendors.isSendSms` | `string` | | Optional. set the account user to recieve sms emergency notifications. if supported for the customer.
`vendors.phone` | `string` | | Optional. set the account user land line phone number which can also be used for IVR based notifications if vendors.isSendIvr is set to true
`vendors.isSendIvr` | `string` | | Optional. set the account user to recieve IVR based notifications if supported for the customer.
`vendors.email` | `string` | | Optional. set the account user email address which can also be used for email based notifications if vendors.isSendEmail is set to true
`vendors.isSendEmail` | `string` | | Optional. set the account user to recieve email based notifications if supported for the customer.
`name` | `string` | | Optional. The user name
`language` | `string` | | Optional. The locale for the user which will also be the language that a message will be sent from the server. the supported languages are available via Content recieved from url created from getContent.(content id : 'LANGUAGES')
`segments` | `array` | | Optional. The registration area codes that the user want to recieve notifications which are in their geo location.
`relations` | `array` | | Optional. The relation group codes that the user want to recieve notifications which are in sent to them.



##### Example

```javascript
if(Evigilo && Evigilo.postSettings) {
                    var settings = {};
                    settings.name = 'eVigilo user';
                    settings.vendors = {mobile : '031111111'};
                    settings.language = en_US; // locale
                    Evigilo.postSettings(settings,function(){
                        console.log("Succeed updating server");
                    },function(e){
                      console.log("Failed updating server");                     
                    });
          }
```
