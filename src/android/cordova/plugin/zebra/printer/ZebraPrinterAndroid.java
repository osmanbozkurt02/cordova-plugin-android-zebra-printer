package cordova.plugin.zebra.printer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix; 
import android.os.Looper;


/**
 * This class echoes a string called from JavaScript.
 */
public class ZebraPrinterAndroid extends CordovaPlugin {

//    @Override
//    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//        if (action.equals("PrintAction")) {
//            String message = args.getString(0);
//            this.coolMethod(message, callbackContext);
//            return true;
//        }
//        return false;
//    }
//
//    private void coolMethod(String message, CallbackContext callbackContext) {
//        if (message != null && message.length() > 0) {
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }
//    }

private Connection connection;



    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)  {
    

        if (action.equals("PrintImage")) {
            String MacAddress = null;
            String ImageUrl = "";
            try {
                MacAddress = args.getString(0);
                 ImageUrl = args.getString(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.PrintImagePreview(MacAddress, ImageUrl, callbackContext);
            return true;
        }else
        if (action.equals("SendCommandToPrinter")) {
            String MacAddress = null;
            String CommandText = "";
            try {
                MacAddress = args.getString(0);
                CommandText = args.getString(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            this.SendCommandToPrinter(CommandText, MacAddress, callbackContext);
            return true;
        } else  if (action.equals("GetPrinterLanguage")) {
            String MacAddress = null;
            try {
                MacAddress = args.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

                this.GetPrinterLanguage(MacAddress, callbackContext);

            return true;
        }
        

        return false;
    }

    private void PrintImagePreview(String MacAddress, String ImageUrl, CallbackContext callbackContext) {
        if (MacAddress != null && MacAddress.length() > 0) {
            PrintImage(callbackContext, ImageUrl, MacAddress);

        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

 


    public String PrintImage(CallbackContext callbackContext, String ImageUrl, String macAddress){
    

        new Thread(new GetBitmapTask(""+ImageUrl, new GetBitmapTask.Callback() {
            @Override public void onFinish(Bitmap bitmap) {
                //here is your bitmap
                printRotatedPhotoFromExternal(bitmap, 0, macAddress, callbackContext);
            }

            @Override public void onError(Throwable t) {
         
            }
        })).start();

        return "";
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }

    }


    private void printRotatedPhotoFromExternal(final Bitmap bitmap, int rotationAngle, String MacAddress, CallbackContext callbackContext) {
        Bitmap rotatedBitmap = rotateBitmap(bitmap, rotationAngle);
        printPhotoFromExternal(rotatedBitmap, MacAddress, callbackContext);
    }

    private Bitmap rotateBitmap(final Bitmap bitmap, int rotationAngle) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }



    private void printPhotoFromExternal(final Bitmap bitmap, String MacAddress, CallbackContext callbackContext) {



        new Thread(new Runnable() {
            public void run() {

                try {
                    Looper.prepare();
                    connection = new BluetoothConnection(MacAddress);
                    connection.open();
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
//                    ZebraPrinterLinkOs linkOsPrinter = null;
//                    try {
//                          linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
//                    }catch (ConnectionException ex){
//
//                    }
//                    catch (Exception ex){
//
//                    }



                //    PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

                    if (true) {
                        try {
                            //   printer.sendCommand("! U1 do \"device.restore_defaults\" \"bluetooth\"");
                             SGD.SET("zpl.label_length",( bitmap.getHeight()),connection);
                            printer.printImage(new ZebraImageAndroid(bitmap), 0, 0, 0, 0, false);
                            callbackContext.success("");


                        } catch (ConnectionException e) {
                            callbackContext.error("" + e.getMessage());
                        }
                    }
//                    else if (printerStatus.isHeadOpen) {
//
//                        callbackContext.error("Lütfen Yazdırmak için Yazıcı Kafasını Kapatın.");
//                    } else if (printerStatus.isPaused) {
//
//                        callbackContext.error("Yazıcı durdurulmuşttur.");
//
//                    }
//                    else if (printerStatus.isPaperOut) {
//
//                        callbackContext.error("Yazıcının kağıdı bitmiştir.");
//                    }
                    else {

                        callbackContext.error("Lütfen yazıcı bağlantınızı kontrol edin.");
                    }

                    connection.close();


                } catch (ConnectionException e) {


                } catch (ZebraPrinterLanguageUnknownException e) {

                } finally {
                    bitmap.recycle();

                    Looper.myLooper().quit();

                }
            }

        }).start();

    }


    private void GetPrinterLanguage(String MacAddress, CallbackContext callbackContext)  {

        connection = new BluetoothConnection(MacAddress);
        try {
            connection.open();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

         String printerLanguage = "";
        try {
            printerLanguage = SGD.GET("device.languages", connection);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        final String displayPrinterLanguage = "Printer Language is " + printerLanguage;


        JSONObject obj = new JSONObject();
        try {
            obj.put("printerLanguage", ""+connection);
            obj.put("displayPrinterLanguage", ""+printerLanguage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        callbackContext.success(obj);

    }




    private void SendCommandToPrinter(String CommandText, String MacAddress, CallbackContext callbackContext) {


        new Thread(new Runnable() {
            public void run() {

                try {
                    Looper.prepare();
                    connection = new BluetoothConnection(MacAddress);
                    connection.open();
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                 //   ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

                  //  PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

                    if (true) {
                        try {
                            printer.sendCommand(CommandText);
                            callbackContext.success("");


                        } catch (ConnectionException e) {
                            callbackContext.error("" + e.getMessage());
                        }
                    } 
//                    else if (printerStatus.isHeadOpen) {
//
//                        callbackContext.error("Lütfen Yazdırmak için Yazıcı Kafasını Kapatın.");
//                    } else if (printerStatus.isPaused) {
//
//                        callbackContext.error("Yazıcı durdurulmuşttur.");
//
//                    } else if (printerStatus.isPaperOut) {
//
//                        callbackContext.error("Yazıcının kağıdı bitmiştir.");
//                    } 
                    else {

                        callbackContext.error("Lütfen yazıcı bağlantınızı kontrol edin.");
                    }

                    connection.close();


                } catch (ConnectionException e) {


                } catch (ZebraPrinterLanguageUnknownException e) {

                } finally {


                    Looper.myLooper().quit();

                }
            }

        }).start();


    }





    public static class GetBitmapTask implements Runnable {

        private final String uri;
        private final Callback callback;

        public GetBitmapTask(String uri, Callback callback) {
            this.uri = uri;
            this.callback = callback;
        }

        @Override public void run() {
            try {
                URL url = new URL(uri);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                callback.onFinish(bmp);
            } catch (IOException e) {
                callback.onError(e);
            }
        }

        public interface Callback{
            void onFinish(Bitmap bitmap);
            void onError(Throwable t);
        }
    }


























}
