package com.rwz.smartrobot;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class HookMain implements IXposedHookLoadPackage {

    private static final String TAG = "TAG";

    private static final String WX_PACKAGE_NAME = "com.tencent.mm";
    public static final String WE_APP_CLAZZ = "com.tencent.tinker.loader.app.TinkerApplication";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
//        Log.d(TAG, "HookMain, packageName: " + packageName);
        if (!WX_PACKAGE_NAME.equals(packageName))
            return;
        Log.d(TAG, "HookMain, findAndHookMethod start");

        final ClassLoader classLoader = lpparam.classLoader;
        findAndHookMethod(WE_APP_CLAZZ, classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context  mWeChatBaseContext = (Context) param.args[0];
                Log.d(TAG, "attachBaseContext: " + mWeChatBaseContext.getClassLoader());
                //钱包
//                String c2 = "com.tencent.mm.plugin.mall.ui.MallIndexUI";
                //收款
                final String c2 = "com.tencent.wcdb.database.SQLiteDatabase";
                // public final long insert(String str, String str2, ContentValues contentValues) {
                //        try {
                //            return insertWithOnConflict(str, str2, contentValues, 0);
                //        } catch (SQLiteDatabaseCorruptException e) {
                //            throw e;
                //        } catch (SQLException e2) {
                //            Log.e(TAG, "Error inserting %s: %s", contentValues, e2);
                //            return -1;
                //        }
                //    }

                //  private static final String[] CONFLICT_VALUES = new String[]{"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
                //public final long insertWithOnConflict(String str, String str2, ContentValues contentValues, int i) {
                //        int i2 = 0;
                //        acquireReference();
                //        SQLiteStatement sQLiteStatement;
                //        try {
                //            StringBuilder stringBuilder = new StringBuilder();
                //            stringBuilder.append("INSERT");
                //            stringBuilder.append(CONFLICT_VALUES[i]);
                //            stringBuilder.append(" INTO ");
                //            stringBuilder.append(str);
                //            stringBuilder.append('(');
                //            Object[] objArr = null;
                //            int size = (contentValues == null || contentValues.size() <= 0) ? 0 : contentValues.size();
                //            if (size > 0) {
                //                Object[] objArr2 = new Object[size];
                //                int i3 = 0;
                //                for (String str3 : keySet(contentValues)) {
                //                    stringBuilder.append(i3 > 0 ? "," : "");
                //                    stringBuilder.append(str3);
                //                    int i4 = i3 + 1;
                //                    objArr2[i3] = contentValues.get(str3);
                //                    i3 = i4;
                //                }
                //                stringBuilder.append(')');
                //                stringBuilder.append(" VALUES (");
                //                while (i2 < size) {
                //                    stringBuilder.append(i2 > 0 ? ",?" : "?");
                //                    i2++;
                //                }
                //                objArr = objArr2;
                //            } else {
                //                stringBuilder.append(str2 + ") VALUES (NULL");
                //            }
                //            stringBuilder.append(')');
                //            sQLiteStatement = new SQLiteStatement(this, stringBuilder.toString(), objArr);
                //            long executeInsert = sQLiteStatement.executeInsert();
                //            sQLiteStatement.close();
                //            releaseReference();
                //            return executeInsert;
                //        } catch (Throwable th) {
                //            releaseReference();
                //        }
                //    }
                XposedHelpers.findAndHookMethod(c2, classLoader,"insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object[] args = param.args;
                        //message
                        //msgId
                        //bizClientMsgId=
                        // msgId=31
                        // msgSvrId=2729473359186899796
                        // talker=wxid_ttkg1apkiy2o21
                        // content=好想
                        // flag=0
                        // status=3
                        // msgSeq=620231072
                        // createTime=1543543953000
                        // lvbuffer=[B@34d2827c
                        // isSend=0
                        // type=1
                        // bizChatId=-1
                        // talkerId=30
                        if (args == null || args.length != 3) {
                            return;
                        }
                        Log.d(TAG, "afterHookedMethod: " + args[0] + "\n" + args[1] + "\n" + args[2]);
                        ContentValues values = (ContentValues) args[2];
                        if (true) {
                            return;
                        }
                        int isSend = (int) values.get("isSend");
                        //说话人ID
                        final String strTalker = values.getAsString("talker");
                        //收到消息，进行回复（要判断不是自己发送的、不是群消息、不是公众号消息，才回复）
                        if (isSend != 1 && !strTalker.endsWith("@chatroom") && !strTalker.startsWith("gh_")) {
                            Object content = values.get("content");
                            OkHttpManager.getInstance().request(content + "", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    //"values": {
                                    //                //			"text": "仅仅是一个打招呼，我能开心好久好久"
                                    //                //		}
                                    //                //	}]
                                    //                //}
                                    String string = response.body().string();
                                    String content = "你说了什么，没听见……";
                                    if (!TextUtils.isEmpty(string)) {
                                        String key = "\"text\":\"";
                                        int len = key.length();
                                        int index = string.lastIndexOf(key);
                                        int end = string.lastIndexOf("\"");
                                        Log.d(TAG, "onResponse: " + "index = " + index + ", end = " + end + ", len = " + len);
                                        if (end > index + len && index > -1) {
                                            content = string.substring(index + len, end);
                                        }
                                    }
                                    Log.d(TAG, "onResponse: " + content + " , strTalker = " + strTalker + " \n " + string);
                                    replyTextMessage(classLoader, content, strTalker);
                                }
                            });
                        }
                    }
                });

            }
        });
        Log.d(TAG, "HookMain, findAndHookMethod end");
    }


    //回复文本消息
    public static void replyTextMessage(ClassLoader classLoader,
                                        String strContent, final String strChatroomId) {
        XposedBridge.log("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);
        if (strContent == null || strChatroomId == null
                || strContent.length() == 0 || strChatroomId.length() == 0) {
            return;
        }
        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.i", classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar,
                new Class[]{String.class, String.class, int.class, int.class, Object.class},
                strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
                    put(strChatroomId, strChatroomId);
                }});
        Object[] objectParamiVar = new Object[]{objectiVar, 0};
        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");

        //查找au.DF().a()方法
        Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.ab.o", classLoader);
        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ab.l", classLoader);
        Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);
        //调用发消息方法
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
            XposedBridge.log("invokeOriginalMethod()执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    }

}
