package cn.edu.haust.starweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.print("请注意：网络状态发生变化：");
        //检测API是否小于21，因为API 21之后，getNetworkInfo(int networkType)方法被弃用
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Toast.makeText(context,"WIFI已连接，移动数据已连接",Toast.LENGTH_SHORT).show();
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Toast.makeText(context,"WIFI已连接，移动数据已断开",Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()){
                Toast.makeText(context,"WIFI已断开，移动数据已连接",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context,"WIFI已断开，移动数据已断开",Toast.LENGTH_SHORT).show();
            }
        } else {
            System.out.println("API level 大于 21");
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connMgr.getAllNetworks();
            NetworkInfo networkInfo;
            boolean connected = false;
//            StringBuilder netStr = new StringBuilder();
            //检查网络是否连接成功（亦可以显示具体的网络连接内容）
            for (Network mNetwork : networks) {
                networkInfo = connMgr.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED) ) {
                    connected = true;
/*                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                        //  Toast.makeText(context,"WIFI已连接",Toast.LENGTH_SHORT).show();
                        netStr.append("WIFI已连接");
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        netStr.append("移动数据已连接");
                    }*/
                }
            }
            if (!connected) {
//                netStr.append("网络未连接，无法获取天气信息，请检查相关设置!");
                Toast.makeText(context, "网络未连接，无法获取天气信息，请检查手机网络设置!", Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(context, netStr.toString(),Toast.LENGTH_SHORT).show();

        }
    }
}
