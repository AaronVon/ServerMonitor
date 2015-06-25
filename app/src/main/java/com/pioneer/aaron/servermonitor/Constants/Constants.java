package com.pioneer.aaron.servermonitor.Constants;

/**
 * Created by Aaron on 6/14/15.
 */
public interface Constants {
    //global
    String POST_URL = "https://longview.linode.com/fetch";

    String KEY_API = "api_key";
    String KEY_START = "start";
    String KEY_END = "end";
    String KEY_ACTION = "api_action";
    String KEY_KEYS = "keys";
    String VALUE_API = "F6F3C79A-A931-D5A0-789863E7C7A84825";
    String VALUE_ACTION = "getValues";
    //cpu
    String CPU_VALUE_KEYS = "[\"CPU.*.wait\",\"CPU.*.user\",\"CPU.*.system\"]";

    //memory
    String MEMORY_VALUE_KEYS = "[\"Memory.real.used\",\"Memory.real.cache\",\"Memory.real.buffers\",\"Memory.swap.used\",\"Memory.real.free\"]";

    //network
    String NETWORK_VALUE_KEYS = "[\"Network.Interface.*.rx_bytes\",\"Network.Interface.*.tx_bytes\"]";

    //disk
    String DISK_VALUE_KEYS = "[\"Disk.*.reads\",\"Disk.*.writes\",\"Disk.*.isswap\",\"SysInfo.type\"]";

    //process
    String PROCESS_VALUE_KEYS = "[\"Uptime\",\"SysInfo.*\",\"Packages.*\"]";

    //SharedPreferences
    String SHAREDPREFS = "sharedPres";
    String INITIALIZED = "isFirstRun";
    String REFRESHGAP = "refreshgap";
    String SETTINGTIP = "setTipIsFirst";
}
