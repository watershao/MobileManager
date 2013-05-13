package com.qrobot.mobilemanager.bt.util;

import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;

public class WifiParam {

	//ScanResult
	
    /** The network name. */
    public final static String SCANRESULT_SSID = "SSID";
    /** The address of the access point. */
    public final static String SCANRESULT_BSSID = "BSSID";
    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    public final static String SCANRESULT_CAPABILITY = "capabilities";
    /**
     * The detected signal level in dBm. At least those are the units used by
     * the TI driver.
     */
    public final static String SCANRESULT_LEVEL = "level";
    /**
     * The frequency in MHz of the channel over which the client is communicating
     * with the access point.
     */
    public final static String SCANRESULT_FREQUENCY = "frequency";

    /**
     * 当前wifi是否已保存配置
     */
    public final static String SCANRESULT_IS_CONFIGED = "isConfiged";
    
    /**
     * 当前wifi是否连接
     */
    public final static String SCANRESULT_IS_CONNECTED = "isConnected";
    
    //wifi configuration param
    
    /**
     * The ID number that the supplicant uses to identify this
     * network configuration entry. This must be passed as an argument
     * to most calls into the supplicant.
     */
    public final static String WIFICONFIG_NETWORDID = "networkId";
    
    /**
     * The network's SSID. Can either be an ASCII string,
     * which must be enclosed in double quotation marks
     * (e.g., {@code "MyNetwork"}, or a string of
     * hex digits,which are not enclosed in quotes
     * (e.g., {@code 01a243f405}).
     */
    public final static String WIFICONFIG_SSID = "SSID";
    /**
     * When set, this network configuration entry should only be used when
     * associating with the AP having the specified BSSID. The value is
     * a string in the format of an Ethernet MAC address, e.g.,
     * <code>XX:XX:XX:XX:XX:XX</code> where each <code>X</code> is a hex digit.
     */
    public final static String WIFICONFIG_BSSID = "BSSID";
    /**
     * Priority determines the preference given to a network by {@code wpa_supplicant}
     * when choosing an access point with which to associate.
     */
    public final static String WIFICONFIG_PRIORITY = "priority";
    /**
     * The set of key management protocols supported by this configuration.
     * See {@link KeyMgmt} for descriptions of the values.
     * Defaults to WPA-PSK WPA-EAP.
     */
    public final static String WIFICONFIG_ALLOWED_KEY_MANAGEMENT = "allowedKeyManagement";
    /**
     * The set of security protocols supported by this configuration.
     * See {@link Protocol} for descriptions of the values.
     * Defaults to WPA RSN.
     */
    public final static String WIFICONFIG_ALLOWED_PROTOCOL = "allowedProtocols";
    
    /**
     * The set of authentication protocols supported by this configuration.
     * See {@link AuthAlgorithm} for descriptions of the values.
     * Defaults to automatic selection.
     */
    public final static String WIFICONFIG_ALLOWED_AUTH_ALGORITHMS = "allowedAuthAlgorithms";
    /**
     * The set of pairwise ciphers for WPA supported by this configuration.
     * See {@link PairwiseCipher} for descriptions of the values.
     * Defaults to CCMP TKIP.
     */
    public final static String WIFICONFIG_ALLOWED_PAIRWISE_CIPHERS =  "allowedPairwiseCiphers";
    /**
     * The set of group ciphers supported by this configuration.
     * See {@link GroupCipher} for descriptions of the values.
     * Defaults to CCMP TKIP WEP104 WEP40.
     */
    public final static String WIFICONFIG_ALLOWED_GROUP_CIPHERS = "allowedGroupCiphers";
    
    
    /**
     * 读取消息的默认文本类型
     */
    public static final int MESSAGE_TEXT = -1;
    
	//wifi控制的一些指令
    
    public static final int WIFI_OPEN = 0x01;
    public static final int WIFI_CLOSE = 0x02;
    public static final int WIFI_SCAN = 0x03;
    public static final int WIFI_SCAN_RESULTS = 0x04;
    public static final int WIFI_PASSWORD = 0x05;
    public static final int WIFI_STATE = 0x06;
    
    public static final int BLUE_CLOSE = 0x07;
    
    public static final int WIFI_FORGET = 0x08;
    
}
