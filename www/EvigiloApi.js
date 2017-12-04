'use strict';

var exec = require('cordova/exec');

function Evigilo() {
    this._eventsListeners = [];
    this.EVENT_NEW_MESSAGE = "NATIVE_EVENT_NEW_MESSAGE";
    this.EVENT_REGISTRATION = "NATIVE_EVENT_REGISTRATION";
    this._pendingData = [];
};

Evigilo.prototype.init = function (initObj, failureCallback) {
    // wait at least one process tick to allow event subscriptions
    var ref = this;
    setTimeout(function () {
        //fire
        if (!initObj.hasOwnProperty("push")) {
            initObj.push = {};
        }
        exec(function(data){
            if(ref._eventsListeners[data.eventType] && ref._eventsListeners[data.eventType].length > 0){
                ref._dispatchNativeEvent(data)
            } else {
                if (!ref._pendingData[data.eventType]) {
                        ref._pendingData[data.eventType] = [];
                }
                ref._pendingData[data.eventType].push(data);
            }
        },
            failureCallback,
            'EvigiloApi',
            'initialize', [initObj]
        );
    }, 10);

};


Evigilo.prototype.sendEvent = function (eventObj, success, failure) {
    // fire
    exec(
        success,
        failure,
        'EvigiloApi',
        'sendEvent', [eventObj]
    );
};

Evigilo.prototype.postSettings = function (settingObj, success, failure) {
    // fire
    exec(
        success,
        failure,
        'EvigiloApi',
        'postSettings', [settingObj]
    );
};

Evigilo.prototype.registerDevice = function (token, success, failure) {
    // fire
    var device = {}
    device.token = token;
    exec(
        success,
        failure,
        'EvigiloApi',
        'registerDevice', [device]
    );
};

Evigilo.prototype.sendFeedback = function (messageId, feedback, success, failure) {
    // fire
    var feedbackObj = {}
    feedbackObj.messageId = messageId;
    feedbackObj.feedback = feedback;
    exec(
        success,
        failure,
        'EvigiloApi',
        'sendFeedback', [feedbackObj]
    );
};


Evigilo.prototype.getMessages = function (success, failure) {
    // fire
    exec(
        success,
        failure,
        'EvigiloApi',
        'getMessages', []
    );
};

Evigilo.prototype.contentUrl = function (contentType, success, failure) {
    // fire
    var content = {}
    content.contentType = contentType;
    exec(
        success,
        failure,
        'EvigiloApi',
        'contentUrl', [content]
    );
};




Evigilo.prototype._dispatchNativeEvent = function (data) {
    console.log("dispatchEvent - " + data.eventType);
    if (this._eventsListeners[data.eventType]) {
        for (var i = this._eventsListeners[data.eventType].length - 1; i >= 0; i--) {
            console.log(this._eventsListeners[data.eventType][i]);
            this._eventsListeners[data.eventType][i].apply(null, data.args);
        }
    }
};

Evigilo.prototype.registerEvent = function (eventType, func) {
    if (!this._eventsListeners[eventType]) {
        this._eventsListeners[eventType] = [];
    }
    this._eventsListeners[eventType].push(func);

    //invoke the pending data which wer got from native before js register
    if (this._pendingData[eventType]) {
        var ref = this;
        this._pendingData[eventType].forEach(function(data) {
                console.log("dispatching pending data + " + data);
                ref._dispatchNativeEvent(data);
        });
    }
    //console.log("register event " + nativeEventManager.eventsListenrs[eventType]);
};


Evigilo.prototype.unRegisterEvent = function (eventType, func) {
    if (this._eventsListeners[eventType]) {
        for (var i = this._eventsListeners[eventType].length - 1; i >= 0; i--) {
            if (this._eventsListeners[eventType][i] === func) {
                this._eventsListeners[eventType].splice(i, 1);
            }
        }
    }
};

module.exports = new Evigilo()
