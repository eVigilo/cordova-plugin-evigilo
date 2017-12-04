package com.evigilo.android.plugin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.evigilo.android.Constants;
import com.evigilo.android.Preferences;
import com.evigilo.android.api.DeviceApi;
import com.evigilo.android.api.event.EmergencyEvent;
import com.evigilo.android.api.event.EmergencyEventApi;
import com.evigilo.android.location.GPSTracker;
import com.evigilo.android.messages.PreferencesReceivedMessages;
import com.evigilo.android.messages.ReceivedMessagesService;
import com.evigilo.android.network.ServiceCallback;
import com.evigilo.android.push.PushPreferences;
import com.evigilo.android.push.PushSupport;
import com.evigilo.android.util.Logger;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;

import static com.evigilo.android.Constants.SMART_TAG;
import static com.evigilo.android.messages.ReceivedMessagesService.*;
import static com.evigilo.android.push.PushConstants.ADDITIONAL_DATA;
import static com.evigilo.android.push.PushConstants.COUNT;
import static com.evigilo.android.push.PushConstants.GCM_DEFAULT_SENDER_ID;
import static com.evigilo.android.push.PushConstants.IMAGE;
import static com.evigilo.android.push.PushConstants.MESSAGE_PARAM_DESC;
import static com.evigilo.android.push.PushConstants.MESSAGE_PARAM_FEEDBACK;
import static com.evigilo.android.push.PushConstants.MESSAGE_PARAM_TITLE;
import static com.evigilo.android.push.PushConstants.SOUND;

/**
 * The evigilo plugin
 * Created by pohes on 10/6/17.
 */

public class EvigiloApiPlugin extends CordovaPlugin implements PluginConstants, PushSupport.PushListener {

    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final JSONObject requestObject = args.optJSONObject(0);

        if (action.equals(INITIALZE_ACTION)) {
            this.callbackContext = callbackContext;
            JSONObject androidPrefs = requestObject.optJSONObject("push");
            registerPush(androidPrefs);
            loadVersions();

            return true;
        } else {
            final Activity activity = cordova.getActivity();
            if (action.equals(SEND_EVENT_ACTION)) {
                EmergencyEvent emergencyEvent = EmergencyEvent.builder()
                        .panicText(requestObject.optString(EVENT_TEXT_REQUEST_PARAM, null))
                        .firstName(requestObject.optString(EVENT_SENDER_NAME_REQUEST_PARAM, null))
                        .phone(requestObject.optString(EVENT_SENDER_PHONE_REQUEST_PARAM, null))
                        .photoPath(requestObject.optString(PHOTO_PATH_REQUEST_PARAM, null))
                        .soundPath(requestObject.optString(EVENT_SOUND_PATH_REQUEST_PARAM, null))
                        .reportType(requestObject.optInt(EVENT_REPORT_TYPE_REQUEST_PARAM))
                        .event(requestObject.optString(EVENT_ID_REQUEST_PARAM, null))
                        .subEvent(requestObject.optString(SUB_EVENT_ID_REQUEST_PARAM, null))
                        .messageId(requestObject.optString(MESSAGE_ID_REQUEST_PARAM, null))
                        .build(activity);
                emergencyEvent.send(new PluginEmergencyEventCallback(callbackContext));
                return true;
            } else if (action.equals(POST_SETTINGS_ACTION)) {
                DeviceApi.getClassInstance().updateAccount(activity
                        , requestObject.optJSONArray(SEGMENTS_REQUEST_PARAM)
                        , requestObject.optJSONObject(VENDORS_REQUEST_PARAM)
                        , requestObject.optJSONArray(RELATIONS_REQUEST_PARAM)
                        , requestObject.optString(LANGUAGE_REQUEST_PARAM,null)
                        , requestObject.optString(NAME_REQUEST_PARAM,null)
                        ,
                        new CordovaServiceCallback(callbackContext) {
                            @Override
                            public void onComplete(JSONObject response) {
                                String lang = requestObject.optString(LANGUAGE_REQUEST_PARAM);
                                if (lang != null) {
                                    Preferences.setAppLanguage(activity, lang);
                                }
                                super.onComplete(response);
                            }
                        }
                );
                return true;
            } else if (action.equals(SEND_FEEDBACK_ACTION)) {

                String messageID = requestObject.optString(MESSAGE_ID_REQUEST_PARAM);
                int feedback = requestObject.optInt(FEEDBACK_REQUEST_PARAM);
                DeviceApi.getClassInstance().sendFeedback(activity
                        , messageID,feedback
                        , GPSTracker.getInstance(cordova.getActivity()).getLastRegisterLocation()
                        , new CordovaServiceCallback(callbackContext)
                );
                return true;
            } else if (action.equals(REGISTER_DEVICE_ACTION)) {
                String token = getToken(requestObject, TOKEN_REQUEST_PARAM, activity);

                DeviceApi.getClassInstance().registerDevice(activity
                        , token
                        , new CordovaServiceCallback(callbackContext)
                );
                return true;
            } else if (action.equals(GET_MESSAGES_ACTION)) {
                JSONArray applicationMessages = new JSONArray();
                ReceivedMessagesService receivedMessages = PreferencesReceivedMessages.getClassInstance(activity);
                applicationMessages.put(receivedMessages.getMessagesByType(null));
                applicationMessages.put(receivedMessages.getMessagesByType(NEWS_TYPE));
                applicationMessages.put(receivedMessages.getMessagesByType(UPDATE_TYPE));
                applicationMessages.put(receivedMessages.getMessagesByType(ALERT_TYPE));
                callbackContext.success(applicationMessages.toString());
                return true;

            } else if (action.equals(CONTENT_URL_ACTION)) {
                String contentType = requestObject.optString(CONTENT_TYPE_REQUEST_PARAM);

//                String language = requestObject.optString(LANGUAGE_REQUEST_PARAM);

                String contentUrl = DeviceApi.getContentUrl(Preferences.getAppLanguage(activity),contentType, activity);


                callbackContext.success(new JSONArray(Collections.singleton(contentUrl)));
            }
        }
        return true;

//        return super.execute(action, args, callbackContext);
    }

    private void loadVersions() {
        //get versions from server (on app open)
        DeviceApi.getClassInstance().getContentVersions(cordova.getActivity(), Preferences.getAppLanguage(cordova.getActivity()), new ServiceCallback() {
            @Override
            public void onComplete(JSONObject response) {
                String data = response.optString("data");
                if (data != null) {
                    try {
                        Preferences.saveContentVersionsForLocale(cordova.getActivity().getApplicationContext(), Preferences.APPLICATION_LANGUAGE, new JSONObject(data));
                    } catch (JSONException e) {
                        Logger.e(SMART_TAG, "could not parse response versions", e);
                    }
                }
//                if (callbackContext != null) {
//                    try {
//                        callbackContext.success(response.getString("data"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onError(Exception e) {
                Logger.e(SMART_TAG, "error getting versions", e);
//                callbackContext.error(e.getMessage());
            }
        });
    }

    private void registerPush(final JSONObject pushPrefs) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String senderID = getStringResourceByName(GCM_DEFAULT_SENDER_ID);
                JSONObject androidPrefs = pushPrefs.optJSONObject("android");

                PushSupport.initialize(cordova.getActivity(), senderID, androidPrefs, EvigiloApiPlugin.this);

            }
        });
    }




    @Override
    public void handleNativeEvent(String eventType, Bundle message) {
        JSONObject event = new JSONObject();

        PluginResult pluginResult;
        try {
            event.put("eventType", eventType);
            event.put("args", convertBundleToJson(message));
            pluginResult = new PluginResult(PluginResult.Status.OK, event);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } catch (Exception e) {
            e.printStackTrace();

            pluginResult = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
            pluginResult.setKeepCallback(true);

            callbackContext.sendPluginResult(pluginResult);
        }
    }

    @Override
    public void sendError(Exception e) {
        callbackContext.error(e.getMessage());
    }

    @Override
    public void onDestroy() {
        PushSupport.clear();
    }

    private static class PluginEmergencyEventCallback implements EmergencyEventApi.EmergencyEventCallback {
        private final CallbackContext callbackContext;

        PluginEmergencyEventCallback(CallbackContext callbackContext) {
            this.callbackContext = callbackContext;
        }

        @Override
        public void eventFailed(Exception e) {
            callbackContext.error(e.getMessage());
        }

        @Override
        public void eventSent(JSONObject paramJSON) {
            callbackContext.success(paramJSON);
        }

        @Override
        public void attachmentError(EmergencyEventApi.AttachmentStatus badFile, Exception e) {
            callbackContext.error(e.getMessage());
        }

        @Override
        public void attachmentSent(int code, String body) {
            try {
                JSONObject jsonObject = new JSONObject(body);
                callbackContext.success(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private static class CordovaServiceCallback implements ServiceCallback {
        private final CallbackContext callbackContext;

        CordovaServiceCallback(CallbackContext callbackContext) {
            this.callbackContext = callbackContext;
        }

        @Override
        public void onComplete(JSONObject response) {
            callbackContext.success(response);
        }

        @Override
        public void onError(Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private static String getToken(JSONObject requestObject, String tokenRequestParam, Activity activity) throws JSONException {
        String token;
        if (requestObject.has(tokenRequestParam) && !requestObject.isNull(tokenRequestParam)) {
            token = requestObject.getString(tokenRequestParam);
        } else {
            final SharedPreferences settings = activity.getSharedPreferences(Constants.SharedPreferencesName, 0);
            String receiveMessages = settings.getString(RECEIVE_MESSAGES, "notFound");
            if (receiveMessages.equals("no")) {
                token = "";
            } else {
                token = PushPreferences.getToken(activity);
            }
        }
        return token;
    }

    /*
     * serializes a bundle to JSON.
     */
    private static JSONObject convertBundleToJson(Bundle extras) {
        Log.d(SMART_TAG, "convert extras to json");
        try {
            JSONObject json = new JSONObject();
            JSONObject additionalData = new JSONObject();

            // Add any keys that need to be in top level json to this set
            HashSet<String> jsonKeySet = new HashSet<String>();
            Collections.addAll(jsonKeySet, MESSAGE_PARAM_TITLE, MESSAGE_PARAM_DESC, COUNT, SOUND, IMAGE);

            for (String key : extras.keySet()) {
                Object value = extras.get(key);

                Log.d(SMART_TAG, "key = " + key);

                if (jsonKeySet.contains(key)) {
                    json.put(key, value);
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    try {
                        // Try to figure out if the value is another JSON object
                        if (strValue.startsWith("{")) {
                            additionalData.put(key, new JSONObject(strValue));
                        }
                        // Try to figure out if the value is another JSON array
                        else if (strValue.startsWith("[")) {
                            additionalData.put(key, new JSONArray(strValue));
                        } else {
                            additionalData.put(key, value);
                        }
                    } catch (Exception e) {
                        additionalData.put(key, value);
                    }
                }
            } // while

            json.put(ADDITIONAL_DATA, additionalData);
            Log.v(SMART_TAG, "extrasToJSON: " + json.toString());

            return json;
        } catch (JSONException e) {
            Log.e(SMART_TAG, "extrasToJSON: JSON exception");
        }
        return null;
    }

    private String getStringResourceByName(String aString) {
        Activity context = cordova.getActivity();
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }


}

