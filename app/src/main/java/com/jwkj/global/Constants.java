package com.jwkj.global;

public class Constants {
    // handle message
    public static final int KEYBOARD_SHOW_FILTER_USER = 0x11;
    public static final String PACKAGE_NAME = "com.nuowei.smarthome.";
    //	public static final String FORGET_PASSWORD_URL = "http://cloudlinks.cn/pw/";
    public static final String FORGET_PASSWORD_URL = "http://www.cloudlinks.cn/pw/";
    public static final String CACHE_FOLDER_NAME = "yoosee";

    public static class P2P {
        // 设备不支持
        public static final String RET_DEVICE_NOT_SUPPORT = PACKAGE_NAME
                + "RET_DEVICE_NOT_SUPPORT";

        // 检查密码
        public static final String ACK_RET_CHECK_PASSWORD = PACKAGE_NAME
                + "ACK_RET_CHECK_PASSWORD";

        // 获取设备各种设置
        public static final String ACK_RET_GET_NPC_SETTINGS = PACKAGE_NAME
                + "ACK_RET_GET_NPC_SETTINGS";

        // 获取布防状态
        public static final String ACK_RET_GET_DEFENCE_STATES = PACKAGE_NAME
                + "ACK_RET_GET_DEFENCE_STATES";

        // 设备时间相关
        public static final String ACK_RET_SET_TIME = PACKAGE_NAME
                + "ACK_RET_SET_TIME";
        public static final String ACK_RET_GET_TIME = PACKAGE_NAME
                + "ACK_RET_GET_TIME";
        public static final String RET_SET_TIME = PACKAGE_NAME + "RET_SET_TIME";
        public static final String RET_GET_TIME = PACKAGE_NAME + "RET_GET_TIME";

        // 视频格式相关
        public static final String ACK_RET_SET_VIDEO_FORMAT = PACKAGE_NAME
                + "ACK_RET_SET_VIDEO_FORMAT";
        public static final String RET_SET_VIDEO_FORMAT = PACKAGE_NAME
                + "RET_SET_VIDEO_FORMAT";
        public static final String RET_GET_VIDEO_FORMAT = PACKAGE_NAME
                + "RET_GET_VIDEO_FORMAT";

        // 音量大小相关
        public static final String ACK_RET_SET_VIDEO_VOLUME = PACKAGE_NAME
                + "ACK_RET_SET_VIDEO_VOLUME";
        public static final String RET_SET_VIDEO_VOLUME = PACKAGE_NAME
                + "RET_SET_VIDEO_VOLUME";
        public static final String RET_GET_VIDEO_VOLUME = PACKAGE_NAME
                + "RET_GET_VIDEO_VOLUME";

        // 修改设备密码相关
        public static final String ACK_RET_SET_DEVICE_PASSWORD = PACKAGE_NAME
                + "ACK_RET_SET_DEVICE_PASSWORD";
        public static final String RET_SET_DEVICE_PASSWORD = PACKAGE_NAME
                + "RET_SET_DEVICE_PASSWORD";

        // 设置网络类型相关
        public static final String ACK_RET_SET_NET_TYPE = PACKAGE_NAME
                + "ACK_RET_SET_NET_TYPE";
        public static final String RET_SET_NET_TYPE = PACKAGE_NAME
                + "RET_SET_NET_TYPE";
        public static final String RET_GET_NET_TYPE = PACKAGE_NAME
                + "RET_GET_NET_TYPE";

        // 设置WIFI相关
        public static final String ACK_RET_SET_WIFI = PACKAGE_NAME
                + "ACK_RET_SET_WIFI";
        public static final String ACK_RET_GET_WIFI = PACKAGE_NAME
                + "ACK_GET_SET_WIFI";
        public static final String RET_SET_WIFI = PACKAGE_NAME + "RET_SET_WIFI";
        public static final String RET_GET_WIFI = PACKAGE_NAME + "RET_GET_WIFI";

        // 设置绑定报警ID
        public static final String ACK_RET_SET_BIND_ALARM_ID = PACKAGE_NAME
                + "ACK_RET_SET_BIND_ALARM_ID";
        public static final String ACK_RET_GET_BIND_ALARM_ID = PACKAGE_NAME
                + "ACK_RET_GET_BIND_ALARM_ID";
        public static final String RET_SET_BIND_ALARM_ID = PACKAGE_NAME
                + "RET_SET_BIND_ALARM_ID";
        public static final String RET_GET_BIND_ALARM_ID = PACKAGE_NAME
                + "RET_GET_BIND_ALARM_ID";
        public static final String DELETE_BINDALARM_ID = PACKAGE_NAME
                + "DELETE_BINDALARM_ID";

        // 设置报警邮箱
        public static final String ACK_RET_SET_ALARM_EMAIL = PACKAGE_NAME
                + "ACK_RET_SET_ALARM_EMAIL";
        public static final String ACK_RET_GET_ALARM_EMAIL = PACKAGE_NAME
                + "ACK_RET_GET_ALARM_EMAIL";
        public static final String RET_SET_ALARM_EMAIL = PACKAGE_NAME
                + "RET_SET_ALARM_EMAIL";
        public static final String RET_GET_ALARM_EMAIL = PACKAGE_NAME
                + "RET_GET_ALARM_EMAIL";
        public static final String RET_GET_ALARM_EMAIL_WITHSMTP = PACKAGE_NAME
                + "RET_GET_ALARM_EMAIL_WITHSMTP";
        public static final String ACK_RET_GET_ALARM_EMAIL_WITHSMTP = PACKAGE_NAME
                + "ACK_RET_GET_ALARM_EMAIL_WITHSMTP";

        // 设置移动侦测
        public static final String ACK_RET_SET_MOTION = PACKAGE_NAME
                + "ACK_RET_SET_MOTION";
        public static final String RET_SET_MOTION = PACKAGE_NAME
                + "RET_SET_MOTION";
        public static final String RET_GET_MOTION = PACKAGE_NAME
                + "RET_GET_MOTION";

        // 设置蜂鸣器
        public static final String ACK_RET_SET_BUZZER = PACKAGE_NAME
                + "RET_SET_BUZZER";
        public static final String RET_SET_BUZZER = PACKAGE_NAME
                + "RET_SET_BUZZER";
        public static final String RET_GET_BUZZER = PACKAGE_NAME
                + "RET_GET_BUZZER";

        // 设置录像模式
        public static final String ACK_RET_SET_RECORD_TYPE = PACKAGE_NAME
                + "ACK_RET_SET_RECORD_TYPE";
        public static final String RET_SET_RECORD_TYPE = PACKAGE_NAME
                + "RET_SET_RECORD_TYPE";
        public static final String RET_GET_RECORD_TYPE = PACKAGE_NAME
                + "RET_GET_RECORD_TYPE";

        // 设置录像时长
        public static final String ACK_RET_SET_RECORD_TIME = PACKAGE_NAME
                + "ACK_RET_SET_RECORD_TIME";
        public static final String RET_SET_RECORD_TIME = PACKAGE_NAME
                + "RET_SET_RECORD_TIME";
        public static final String RET_GET_RECORD_TIME = PACKAGE_NAME
                + "RET_GET_RECORD_TIME";

        // 设置录像计划时间
        public static final String ACK_RET_SET_RECORD_PLAN_TIME = PACKAGE_NAME
                + "ACK_RET_SET_RECORD_PLAN_TIME";
        public static final String RET_SET_RECORD_PLAN_TIME = PACKAGE_NAME
                + "RET_SET_RECORD_PLAN_TIME";
        public static final String RET_GET_RECORD_PLAN_TIME = PACKAGE_NAME
                + "RET_GET_RECORD_PLAN_TIME";

        // 防区设置
        public static final String ACK_RET_SET_DEFENCE_AREA = PACKAGE_NAME
                + "ACK_RET_SET_DEFENCE_AREA";
        public static final String ACK_RET_GET_DEFENCE_AREA = PACKAGE_NAME
                + "ACK_RET_GET_DEFENCE_AREA";
        public static final String ACK_RET_CLEAR_DEFENCE_AREA = PACKAGE_NAME
                + "ACK_RET_CLEAR_DEFENCE_AREA";
        public static final String RET_CLEAR_DEFENCE_AREA = PACKAGE_NAME
                + "RET_CLEAR_DEFENCE_AREA";
        public static final String RET_SET_DEFENCE_AREA = PACKAGE_NAME
                + "RET_SET_DEFENCE_AREA";
        public static final String RET_GET_DEFENCE_AREA = PACKAGE_NAME
                + "RET_GET_DEFENCE_AREA";

        // 远程设置
        public static final String ACK_RET_SET_REMOTE_DEFENCE = PACKAGE_NAME
                + "ACK_RET_SET_REMOTE_DEFENCE";
        public static final String RET_SET_REMOTE_DEFENCE = PACKAGE_NAME
                + "RET_SET_REMOTE_DEFENCE";
        public static final String RET_GET_REMOTE_DEFENCE = PACKAGE_NAME
                + "RET_GET_REMOTE_DEFENCE";

        public static final String ACK_RET_SET_REMOTE_RECORD = PACKAGE_NAME
                + "ACK_RET_SET_REMOTE_RECORD";
        public static final String RET_SET_REMOTE_RECORD = PACKAGE_NAME
                + "RET_SET_REMOTE_RECORD";
        public static final String RET_GET_REMOTE_RECORD = PACKAGE_NAME
                + "RET_GET_REMOTE_RECORD";

        // 设置初始密码
        public static final String ACK_RET_SET_INIT_PASSWORD = PACKAGE_NAME
                + "ACK_RET_SET_INIT_PASSWORD";
        public static final String RET_SET_INIT_PASSWORD = PACKAGE_NAME
                + "RET_SET_INIT_PASSWORD";
        public static final String RET_GET_VISTOR_PASSWORD = PACKAGE_NAME
                + "RET_GET_VISTOR_PASSWORD";

        // 查询录像文件
        public static final String ACK_RET_GET_PLAYBACK_FILES = PACKAGE_NAME
                + "ACK_RET_GET_PLAYBACK_FILES";
        public static final String RET_GET_PLAYBACK_FILES = PACKAGE_NAME
                + "RET_GET_PLAYBACK_FILES";

        // 回放改变进度条
        public static final String PLAYBACK_CHANGE_SEEK = PACKAGE_NAME
                + "PLAYBACK_CHANGE_SEEK";

        // 回放状态改变
        public static final String PLAYBACK_CHANGE_STATE = PACKAGE_NAME
                + "PLAYBACK_CHANGE_STATE";

        // 画布状态切换
        public static final String P2P_CHANGE_IMAGE_TRANSFER = PACKAGE_NAME
                + "P2P_CHANGE_IMAGE_TRANSFER";

        // 设备检查更新
        public static final String ACK_RET_GET_DEVICE_INFO = PACKAGE_NAME
                + "ACK_RET_GET_DEVICE_INFO";
        public static final String RET_GET_DEVICE_INFO = PACKAGE_NAME
                + "RET_GET_DEVICE_INFO";

        public static final String ACK_RET_CHECK_DEVICE_UPDATE = PACKAGE_NAME
                + "ACK_RET_CHECK_DEVICE_UPDATE";
        public static final String RET_CHECK_DEVICE_UPDATE = PACKAGE_NAME
                + "RET_CHECK_DEVICE_UPDATE";

        public static final String ACK_RET_DO_DEVICE_UPDATE = PACKAGE_NAME
                + "ACK_RET_DO_DEVICE_UPDATE";
        public static final String RET_DO_DEVICE_UPDATE = PACKAGE_NAME
                + "RET_DO_DEVICE_UPDATE";

        public static final String ACK_RET_CANCEL_DEVICE_UPDATE = PACKAGE_NAME
                + "ACK_RET_CANCEL_DEVICE_UPDATE";
        public static final String RET_CANCEL_DEVICE_UPDATE = PACKAGE_NAME
                + "RET_CANCEL_DEVICE_UPDATE";
        /*
         * p2p Connect
         */
        public static final String P2P_REJECT = PACKAGE_NAME + "P2P_REJECT";
        public static final String P2P_ACCEPT = PACKAGE_NAME + "P2P_ACCEPT";
        public static final String P2P_READY = PACKAGE_NAME + "P2P_READY";

        // p2p视频分辨率改变
        public static final String P2P_RESOLUTION_CHANGE = PACKAGE_NAME
                + "P2P_RESOLUTION_CHANGE";

        // P2P监控人数改变
        public static final String P2P_MONITOR_NUMBER_CHANGE = PACKAGE_NAME
                + "P2P_MONITOR_NUMBER_CHANGE";
        // 设置图像倒转
        public static final String RET_GET_IMAGE_REVERSE = PACKAGE_NAME
                + "RET_GET_IMAGE_REVERSE";
        public static final String ACK_VRET_SET_IMAGEREVERSE = PACKAGE_NAME
                + "ACK_VRET_SET_IMAGEREVERSE";
        public static final String RET_SET_IMGEREVERSE = PACKAGE_NAME
                + "RET_SET_IMGEREVERSE";
        // 人体红外开关
        public static final String ACK_RET_SET_INFRARED_SWITCH = PACKAGE_NAME
                + "ACK_RET_SET_INFRARED_SWITCH";
        public static final String RET_GET_INFRARED_SWITCH = PACKAGE_NAME
                + "RET_GET_INFRARED_SWITCH";
        // 有线报警输入开关
        public static final String ACK_RET_SET_WIRED_ALARM_INPUT = PACKAGE_NAME
                + "ACK_RET_GET_WIRED_ALARM_INPUT";
        public static final String RET_GET_WIRED_ALARM_INPUT = PACKAGE_NAME
                + "RET_GET_WIRED_ALARM_INPUT";
        // 无线报警输入开关
        public static final String ACK_RET_SET_WIRED_ALARM_OUT = PACKAGE_NAME
                + "ACK_RET_GET_WIRED_ALARM_OUT";
        public static final String RET_GET_WIRED_ALARM_OUT = PACKAGE_NAME
                + "RET_GET_WIRED_ALARM_OUT";
        // 自动升级
        public static final String ACK_RET_SET_AUTOMATIC_UPGRADE = PACKAGE_NAME
                + "ACK_RET_GET_AUTOMATIC_UPGRADE";
        public static final String RET_GET_AUTOMATIC_UPGRAD = PACKAGE_NAME
                + "RET_GET_AUTOMATIC_UPGRAD";
        // 设置访客密码
        public static final String ACK_RET_SET_VISITOR_DEVICE_PASSWORD = PACKAGE_NAME
                + "ACK_RET_SET_VISITOR_DEVICE_PASSWORD";
        public static final String RET_SET_VISITOR_DEVICE_PASSWORD = PACKAGE_NAME
                + "RET_SET_VISITOR_DEVICE_PASSWORD";
        // 设置时区
        public static final String ACK_RET_SET_TIME_ZONE = PACKAGE_NAME
                + "ACK_RET_SET_TIME_ZONE";
        public static final String RET_GET_TIME_ZONE = PACKAGE_NAME
                + "RET_GET_TIME_ZONE";
        // SD卡
        public static final String RET_GET_SD_CARD_CAPACITY = PACKAGE_NAME
                + "RET_GET_SD_CARD_CAPACITY";
        public static final String RET_GET_SD_CARD_FORMAT = PACKAGE_NAME
                + "RET_GET_SD_CARD_FORMAT";
        public static final String ACK_GET_SD_CARD_CAPACITY = PACKAGE_NAME
                + "ACK_GET_SD_CARD_CAPACITY";
        public static final String ACK_GET_SD_CARD_FORMAT = PACKAGE_NAME
                + "ACK_GET_SD_CARD_FORMAT";
        public static final String RET_GET_USB_CAPACITY = PACKAGE_NAME
                + "RET_GET_USB_CAPACITY";
        // 预录像
        public static final String RET_GET_PRE_RECORD = PACKAGE_NAME
                + "RET_GET_PRE_RECORD";
        public static final String ACK_RET_SET_PRE_RECORD = PACKAGE_NAME
                + "ACK_RET_SET_PRE_RECORD";
        public static final String RET_SET_PRE_RECORD = PACKAGE_NAME
                + "RET_SET_PRE_RECORD";
        // 传感器开关
        public static final String ACK_RET_GET_SENSOR_SWITCH = PACKAGE_NAME
                + "ACK_RET_GET_SENSOR_SWITCH";
        public static final String ACK_RET_SET_SENSOR_SWITCH = PACKAGE_NAME
                + "ACK_RET_SET_SENSOR_SWITCH";
        public static final String RET_GET_SENSOR_SWITCH = PACKAGE_NAME
                + "RET_GET_SENSOR_SWITCH";
        public static final String RET_SET_SENSOR_SWITCH = PACKAGE_NAME
                + "RET_SET_SENSOR_SWITCH";
        // 门铃
        public static final String RET_CUSTOM_CMD_DISCONNECT = PACKAGE_NAME
                + "RET_CUSTOM_CMD_DISCONNECT";
        // GPIO
        public static final String ACK_RET_SET_GPIO = PACKAGE_NAME
                + "ACK_RET_SET_GPIO";
        public static final String ACK_RET_SET_GPIO1_0 = PACKAGE_NAME
                + "ACK_RET_SET_GPIO1_0";
        public static final String RET_SET_GPIO = PACKAGE_NAME + "RET_SET_GPIO";
        // 获取音频设备型号
        public static final String RET_GET_AUDIO_DEVICE_TYPE = PACKAGE_NAME
                + "RET_GET_AUDIO_DEVICE_TYPE";
        // SetLamp返回
        public static final String SET_LAMP_STATUS = PACKAGE_NAME
                + "SET_LAMP_STATUS";
        // SetLamp返回
        public static final String ACK_SET_LAMP_STATUS = PACKAGE_NAME
                + "ACK_SET_LAMP_STATUS";
        // GetLamp返回
        public static final String GET_LAMP_STATUS = PACKAGE_NAME
                + "GET_LAMP_STATUS";
        // 获取和设置语言
        public static final String RET_GET_LANGUEGE = PACKAGE_NAME
                + "RET_GET_LANGUEGE";
        public static final String RET_SET_LANGUEGE = PACKAGE_NAME
                + "RET_SET_LANGUEGE";
        //获取nvr的设备列表
        public static final String ACK_GET_NVR_IPC_LIST = PACKAGE_NAME
                + "ACK_GET_NVR_IPC_LIST";
        public static final String RET_GET_NVR_IPC_LIST = PACKAGE_NAME
                + "RET_GET_NVR_IPC_LIST";
        //获取变焦变倍
        public static final String RET_GET_FOCUS_ZOOM = PACKAGE_NAME + "RET_GET_FOCUS_ZOOM";
        public static final String ACK_OPEN_DOOR = PACKAGE_NAME + "ACK_OPEN_DOOR";

        //FTP
        public static final String ACK_GET_FTP_INFO = PACKAGE_NAME + "ACK_GET_FTP_INFO";
        //PIR
        public static final String ACK_GET_PIRLight = PACKAGE_NAME + "ACK_GET_PIRLight";
        public static final String ACK_SET_PIRLight = PACKAGE_NAME + "ACK_SET_PIRLight";
        //获取定时布防回调
        public static final String ACK_GET_DEFENCE_WORK_GROUP = PACKAGE_NAME + "ACK_GET_DEFENCE_WORK_GROUP";
        //		  鱼眼功能相关
        public static final String ACK_FISHEYE = PACKAGE_NAME + "ACK_FISHEYE";
        public static final String RET_SET_IPC_WORKMODE = PACKAGE_NAME + "RET_SET_IPC_WORKMODE";
        public static final String RET_SET_SENSER_WORKMODE = PACKAGE_NAME + "RET_SET_SENSER_WORKMODE";
        public static final String RET_SET_SCHEDULE_WORKMODE = PACKAGE_NAME + "RET_SET_SCHEDULE_WORKMODE";
        public static final String RET_DELETE_SCHEDULE = PACKAGE_NAME + "RET_DELETE_SCHEDULE";
        public static final String RET_GET_CURRENT_WORKMODE = PACKAGE_NAME + "RET_GET_CURRENT_WORKMODE";
        public static final String RET_GET_SENSOR_WORKMODE = PACKAGE_NAME + "RET_GET_SENSOR_WORKMODE";
        public static final String RET_GET_SCHEDULE_WORKMODE = PACKAGE_NAME + "RET_GET_SCHEDULE_WORKMODE";
        public static final String RET_SET_ALLSENSER_SWITCH = PACKAGE_NAME + "RET_SET_ALLSENSER_SWITCH";
        public static final String RET_GET_ALLSENSER_SWITCH = PACKAGE_NAME + "RET_GET_ALLSENSER_SWITCH";
        public static final String RET_SET_LOWVOL_TIMEINTERVAL = PACKAGE_NAME + "RET_SET_LOWVOL_TIMEINTERVAL";
        public static final String RET_GET_LOWVOL_TIMEINTERVAL = PACKAGE_NAME + "RET_GET_LOWVOL_TIMEINTERVAL";

        //第二次添加
        public static final String RET_DELETE_ONE_CONTROLER = PACKAGE_NAME + "RET_DELETE_ONE_CONTROLER";
        public static final String RET_DELETE_ONE_SENSOR = PACKAGE_NAME + "RET_DELETE_ONE_SENSOR";
        public static final String RET_CHANGE_CONTROLER_NAME = PACKAGE_NAME + "RET_CHANGE_CONTROLER_NAME";
        public static final String RET_CHANGE_SENSOR_NAME = PACKAGE_NAME + "RET_CHANGE_SENSOR_NAME";
        public static final String RET_INTO_LEARN_STATE = PACKAGE_NAME + "RET_INTO_LEARN_STATE";
        public static final String RET_TURN_SENSOR = PACKAGE_NAME + "RET_TURN_SENSOR";
        public static final String RET_SHARE_TO_MEMBER = PACKAGE_NAME + "RET_SHARE_TO_MEMBER";
        public static final String RET_GOT_SHARE = PACKAGE_NAME + "RET_GOT_SHARE";
        public static final String RET_DEV_RECV_MEMBER_FEEDBACK = PACKAGE_NAME + "RET_DEV_RECV_MEMBER_FEEDBACK";
        public static final String RET_ADMIN_DELETE_ONE_MEMBER = PACKAGE_NAME + "RET_ADMIN_DELETE_ONE_MEMBER";
        public static final String RET_DELETE_DEV = PACKAGE_NAME + "RET_DELETE_DEV";
        public static final String RET_GET_MEMBER_LIST = PACKAGE_NAME + "RET_GET_MEMBER_LIST";
        public static final String RET_SET_ONE_SPECIAL_ALARM = PACKAGE_NAME + "RET_SET_ONE_SPECIAL_ALARM";
        public static final String RET_GET_ALL_SPECIAL_ALARM = PACKAGE_NAME + "RET_GET_ALL_SPECIAL_ALARM";
        public static final String RET_GET_LAMPSTATE = PACKAGE_NAME + "RET_GET_LAMPSTATE";
        public static final String RET_KEEP_CLIENT = PACKAGE_NAME + "RET_KEEP_CLIENT";
        public static final String RET_LED_LIGHT = PACKAGE_NAME + "RET_LED_LIGHT";
        public static final String SET_LED_LIGHT = PACKAGE_NAME + "SET_LED_LIGHT";


        public static final String RET_QRCODE_LEARN = PACKAGE_NAME + "RET_QRCODE_LEARN";
        //获取报警图像
        public static final String RET_GET_ALLARMIMAGE = PACKAGE_NAME + "RET_GET_ALLARMIMAGE";
        public static final String RET_GET_ALLARMIMAGE_PROGRESS = PACKAGE_NAME + "RET_GET_ALLARMIMAGE_PROGRESS";
        //切换AP模式
        public static final String RET_SET_AP_MODE = PACKAGE_NAME + "RET_SET_AP_MODE";
        //是否支持AP模式
        public static final String RET_AP_MODESURPPORT = PACKAGE_NAME + "RET_AP_MODESURPPORT";
        //P2P图像开始显示
        public static final String RET_P2PDISPLAY = PACKAGE_NAME + "RET_P2PDISPLAY";

        public static final String ACK_GET_REMOTE_DEFENCE = PACKAGE_NAME + "ACK_GET_REMOTE_DEFENCE";
        //获取NVR信息
        public static final String RET_GET_NVRINFO = PACKAGE_NAME + "RET_GET_NVRINFO";
        public static final String ACK_GET_NVRINFO = PACKAGE_NAME + "ACK_GET_NVRINFO";

        //商城	    //系统消息
        public static final String RET_NEW_SYSTEM_MESSAGE = PACKAGE_NAME + "RET_NEW_SYSTEM_MESSAGE";
        //变焦变倍
        public static final String RET_GET_FOCUS_ZOOM_POSITION = PACKAGE_NAME + "RET_GET_FOCUS_ZOOM_POSITION";
        public static final String RET_SET_FOCUS_ZOOM_POSITION = PACKAGE_NAME + "RET_SET_FOCUS_ZOOM_POSITION";

        //获取定时布防
        public static final String RET_GET_DEFENCE_WORK_GROUP = PACKAGE_NAME + "RET_GET_DEFENCE_WORK_GROUP";
        //设置定时布防
        public static final String RET_SET_DEFENCE_WORK_GROUP = PACKAGE_NAME + "RET_SET_DEFENCE_WORK_GROUP";
        //设置预置位
        public static final String RET_DELETE_PRESETMOTOROS = PACKAGE_NAME + "RET_DELETE_PRESETMOTOROS";
        public static final String RET_SET_PRESETMOTOROS = PACKAGE_NAME + "RET_SET_PRESETMOTOROS";
        public static final String RET_GET_PRESETMOTOROS = PACKAGE_NAME + "RET_GET_PRESETMOTOROS";
        public static final String RET_GET_IS_PRESETMOTOROS = PACKAGE_NAME + "RET_GET_IS_PRESETMOTOROS";
        public static final String RET_TOSEE_PRESETMOTOROS = PACKAGE_NAME + "RET_TOSEE_PRESETMOTOROS";

        //绑定预设位
        public static final String P2P_SET_ALARM_PRESET_MOTOR_POS = PACKAGE_NAME + "P2P_SET_ALARM_PRESET_MOTOR_POS";
        //FTP
        public static final String RET_GET_FTP_CONFIG_INFO = PACKAGE_NAME + "RET_GET_FTP_CONFIG_INFO";
        public static final String RET_SET_FTP_CONFIG_INFO = PACKAGE_NAME + "RET_SET_FTP_CONFIG_INFO";

        //带名防区的开关
        public static final String RET_GET_DEFENCE_SWITCH = PACKAGE_NAME + "RET_GET_DEFENCE_SWITCH";
        public static final String RET_SET_DEFENCE_SWITCH = PACKAGE_NAME + "RET_SET_DEFENCE_SWITCH";

        //获取设置防区名字
        public static final String RET_GET_DEFENCE_AREA_NAME = PACKAGE_NAME + "RET_GET_DEFENCE_AREA_NAME";
        public static final String RET_SET_DEFENCE_AREA_NAME = PACKAGE_NAME + "RET_SET_DEFENCE_AREA_NAME";
        //Ehome///////////////
        public static final String SET_GPIO_STATUS = PACKAGE_NAME + "SET_GPIO_STATUS";
        public static final String GET_GPIO = PACKAGE_NAME + "GET_GPIO";
        public static final String USER_DEFINDE_MESSAGE = PACKAGE_NAME + "USER_DEFINDE_MESSAGE";
        //Ehome///////////////
        //龙的灯光控制////////////
        public static final String RET_SETPIRLIGHT = PACKAGE_NAME + "RET_SETPIRLIGHT";
        public static final String RET_SETTING_LIGHT_CONTROL = PACKAGE_NAME + "RET_SETTING_LIGHT_CONTROL";
        public static final String RET_GET_LIGHT_CONTROL_SETTING = PACKAGE_NAME + "RET_GET_LIGHT_CONTROL_SETTING";
        //灯光控制//////////////
        public static final String RET_GETFISHINFO = PACKAGE_NAME + "RET_GETFISHINFO";
        //获取预制位ACK回调
        public static final String ACK_RET_PRESET_POS = PACKAGE_NAME + "ACK_RET_PRESET_POS";
        //设备静音ACK回调
        public static final String ACK_RET_KEEP_CLIENT = PACKAGE_NAME + "ACK_RET_KEEP_CLIENT";
        //指示灯
        public static final String ACK_RET_GET_LIGHT = PACKAGE_NAME + "ACK_RET_GET_LIGHT";
        public static final String ACK_RET_SET_LIGHT = PACKAGE_NAME + "ACK_RET_SET_LIGHT";

        public static final String RET_SET_SENSOR_PREPOINT = PACKAGE_NAME + "RET_SET_SENSOR_PREPOINT";
        // 是否支持记忆点
        public static final String GET_PREPOINT_SURPPORTE = PACKAGE_NAME
                + "GET_PREPOINT_SURPPORTE";

        //10分钟自动拍照
        public static final String RET_GET_AUTO_SNAPSHOT_SWITCH = PACKAGE_NAME + "RET_GET_AUTO_SNAPSHOT_SWITCH";
        public static final String RET_SET_AUTO_SNAPSHOT_SWITCH = PACKAGE_NAME + "RET_SET_AUTO_SNAPSHOT_SWITCH";

        //全屏监控黑白彩色切换
        public static final String RET_GET_VIDEO_COLOR_SWITCH = PACKAGE_NAME + "RET_GET_VIDEO_COLOR_SWITCH";
        public static final String RET_SET_VIDEO_COLOR_SWITCH = PACKAGE_NAME + "RET_SET_VIDEO_COLOR_SWITCH";
        //TF卡和录像状态
        public static final String RET_TF_VIDEO_STATE = PACKAGE_NAME + "RET_TF_VIDEO_STATE";

        //移动侦测灵敏度
        public static final String ACK_vRetSetNpcSettingsMotionSens = PACKAGE_NAME
                + "ACK_vRetSetNpcSettingsMotionSens";

        public static final String RET_GET_MOTION_SENS = PACKAGE_NAME
                + "RET_GET_MOTION_SENS";

        public static final String RET_SET_MOTION_SENS = PACKAGE_NAME
                + "RET_SET_MOTION_SENS";
        public static final String RET_GET_DEVICE_TYPE = PACKAGE_NAME
                + "RET_GET_DEVICE_TYPE";
    }

    public static class MsgSection {


        public static int MSG_ID_SETTING_DEVICE_TIME = 1000;
        public static int MSG_ID_GETTING_DEVICE_TIME = 2000;

        public static int MSG_ID_GETTING_NPC_SETTINGS = 3000;
        public static int MSG_ID_SET_REMOTE_DEFENCE = 4000;
        public static int MSG_ID_SET_REMOTE_RECORD = 5000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_FORMAT = 6000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_VIDEO_VOLUME = 7000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_BUZZER = 8000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_MOTION = 9000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TYPE = 10000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_TIME = 11000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_RECORD_PLAN_TIME = 12000;
        public static int MSG_ID_SETTING_NPC_SETTINGS_NET_TYPE = 13000;

        public static int MSG_ID_SETTING_ALARM_EMAIL = 14000;
        public static int MSG_ID_GETTING_ALARM_EMAIL = 15000;

        public static int MSG_ID_SETTING_ALARM_BIND_ID = 16000;
        public static int MSG_ID_GETTING_ALARM_BIND_ID = 17000;

        public static int MSG_ID_SETTING_INIT_PASSWORD = 18000;
        public static int MSG_ID_SETTING_DEVICE_PASSWORD = 19000;
        public static int MSG_ID_CHECK_DEVICE_PASSWORD = 20000;

        public static int MSG_ID_SETTING_DEFENCEAREA = 21000;
        public static int MSG_ID_GETTING_DEFENCEAREA = 22000;

        public static int MSG_ID_SETTING_WIFI = 23000;
        public static int MSG_ID_GETTING_WIFI_LIST = 24000;

        public static int MSG_ID_GETTING_RECORD_FILE_LIST = 25000;
        public static int MSG_ID_SEND_MESSAGE = 26000;
        public static int MSG_ID_SEND_CUSTOM_CMD = 27000;


        public static int MSG_ID_CHECK_DEVICE_UPDATE = 28000;
        public static int MSG_ID_CANCEL_DEVICE_UPDATE = 29000;
        public static int MSG_ID_DO_DEVICE_UPDATE = 30000;
        public static int MSG_ID_GET_DEFENCE_STATE = 31000;
        public static int MSG_ID_GET_DEVICE_VERSION = 32000;

        public static int MSG_ID_CLEAR_DEFENCE_GROUP = 33000;

        public static int MESG_ID_STTING_PIC_REVERSE = 34000;
        public static int MESG_ID_STTING_IR_ALARM_EN = 35000;
        public static int MESG_STTING_ID_EXTLINE_ALARM_IN_EN = 36000;
        public static int MESG_STTING_ID_EXTLINE_ALARM_OUT_EN = 37000;
        public static int MESG_STTING_ID_SECUPGDEV = 38000;
        public static int MESG_STTING_ID_GUEST_PASSWD = 39000;
        public static int MESG_STTING_ID_TIMEZONE = 40000;
        public static int MESG_GET_SD_CARD_CAPACITY = 41000;
        public static int MESG_SD_CARD_FORMAT = 42000;
        public static int MESG_SET_GPIO = 43000;
        public static int MESG_SET_GPI1_0 = 44000;
        public static int MESG_SET_PRE_RECORD = 45000;
        public static int MESG_GET_DEFENCE_AREA_SWITCH = 46000;
        public static int MESG_SET_DEFENCE_AREA_SWITCH = 47000;

        public static int MESG_SET_LAMP = 52000;
        public static int MESG_GET_LAMP = 53000;
        public static int SET_USER_DEFINE_MESG = 54000;

        public static int MESG_GET_PRESET_MOTOR_POS = 48000;
        public static int MESG_SET_PRESET_MOTOR_POS = 49000;
        public static int MESG_PRESET_MOTOR_POS = 50000;
        public static int IP_CONFIG = 51000;
        public static int MESG_GET_ALARM_CENTER_PARAMETER = 52000;
        public static int MESG_SET_ALARM_CENTER_PARAMETER = 53000;
        public static int MESG_DELETE_DEVICEALARMID = 54000;
        public static int CONTROL_CAMERA = 55000;
        public static int MESG_SET_RECEIVE_DOOBELL = 56000;
        public static int MESG_GET_LANGUEGE = 57000;
        public static int MESG_SET_LANGUEGE = 58000;

        //fisheye jump over the last int 58000,startwith 100000
        public static int MSG_ID_FISHEYE_SETTING_WORKMODE_DEFULT = 101000;
        public static int MSG_ID_FISHEYE_SETTING_IPC_WORKMODE = 102000;
        public static int MSG_ID_FISHEYE_SETTING_SENSOR_WORKMODE = 103000;
        public static int MSG_ID_FISHEYE_SETTING_SCHEDULE_WORKMODE = 104000;
        public static int MSG_ID_FISHEYE_DELETE_SCHEDULE = 105000;
        public static int MSG_ID_FISHEYE_GET_CURRENTWORKMODE = 106000;
        public static int MSG_ID_FISHEYE_GET_SENSORWORKMODE = 107000;
        public static int MSG_ID_FISHEYE_WORKMODE_SCHEDULE = 108000;
        public static int MSG_ID_FISHEYE_SETTING_ALL_SENSOR_SWITCH = 109000;
        public static int MSG_ID_FISHEYE_GET_ALL_SENSOR_SWITCH = 110000;
        public static int MSG_ID_FISHEYE_SET_LOW_VOL_TIMEINTERVAL = 111000;
        public static int MSG_ID_FISHEYE_GET_LOW_VOL_TIMEINTERVAL = 112000;
        //第二次添加
        public static int MSG_ID_FISHEYE_DELETE_ONE_CONTROLER = 113000;
        public static int MSG_ID_FISHEYE_DELETE_ONE_SENSOR = 114000;
        public static int MSG_ID_FISHEYE_CHANGE_CONTROLER_NAME = 115000;
        public static int MSG_ID_FISHEYE_CHANGE_SENSOR_NAME = 116000;
        public static int MSG_ID_FISHEYE_INTO_LEARN_STATE = 117000;
        public static int MSG_ID_FISHEYE_TURN_SENSOR = 118000;
        public static int MSG_ID_FISHEYE_SHARE_TO_MEMBER = 119000;
        public static int MSG_ID_FISHEYE_GOT_SHARE_MESG = 120000;
        public static int MSG_ID_FISHEYE_DEV_RECV_MEMBER_FEEDBACK = 121000;
        public static int MSG_ID_FISHEYE_ADMIN_DELETE_ONE_MEMBER = 122000;
        public static int MSG_ID_FISHEYE_DELETE_DEV = 123000;
        public static int MSG_ID_FISHEYE_GET_MEMBER_LIST = 124000;
        public static int MSG_ID_FISHEYE_SET_SPECIAL_ALARM = 125000;
        public static int MSG_ID_FISHEYE_GET_ALL_SPECIAL_ALARM = 126000;
        public static int MSG_ID_FISHEYE_GET_LAMP_STATE = 127000;
        public static int MSG_ID_FISHEYE_KEEP_CLIENT_STATE = 128000;
        public static int MESG_ID_TYPE_QRCODE_LEARN_CODE = 129000;


    }

    public static class P2P_TYPE {
        public static final int P2P_TYPE_CALL = 0;
        public static final int P2P_TYPE_MONITOR = 1;
        public static final int P2P_TYPE_PLAYBACK = 2;
    }

    public static class P2P_CALL {
        public static class CALL_RESULT {
            public static final int CALL_SUCCESS = 0;
            public static final int CALL_PHONE_FORMAT_ERROR = 1;
        }
    }

    public static class P2P_SET {

        public static class ACK_RESULT {
            public static final int ACK_PWD_ERROR = 9999;
            public static final int ACK_NET_ERROR = 9998;
            public static final int ACK_SUCCESS = 9997;
            public static final int ACK_INSUFFICIENT_PERMISSIONS = 9996;
        }

        public static class DEVICE_TIME_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class VIDEO_FORMAT_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int VIDEO_FORMAT_NTSC = 0;
            public static final int VIDEO_FORMAT_PAL = 1;
        }

        public static class VIDEO_VOLUME_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class DEVICE_PASSWORD_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class NET_TYPE_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int NET_TYPE_WIRED = 0;
            public static final int NET_TYPE_WIFI = 1;
        }

        public static class WIFI_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class BIND_ALARM_ID_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class ALARM_EMAIL_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class MOTION_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int MOTION_DECT_ON = 1;
            public static final int MOTION_DECT_OFF = 0;
            public static final int NOT_SUPPORT = 255;//0XFF
        }

        public static class BUZZER_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int BUZZER_SWITCH_ON_ONE_MINUTE = 1;
            public static final int BUZZER_SWITCH_ON_TWO_MINUTE = 2;
            public static final int BUZZER_SWITCH_ON_THREE_MINUTE = 3;
            public static final int BUZZER_SWITCH_OFF = 0;
        }

        public static class RECORD_TYPE_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int RECORD_TYPE_MANUAL = 0;
            public static final int RECORD_TYPE_ALARM = 1;
            public static final int RECORD_TYPE_TIMER = 2;
        }

        public static class RECORD_TIME_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int RECORD_TIME_ONE_MINUTE = 0;
            public static final int RECORD_TIME_TWO_MINUTE = 1;
            public static final int RECORD_TIME_THREE_MINUTE = 2;
        }

        public static class RECORD_PLAN_TIME_SET {
            public static final int SETTING_SUCCESS = 0;

        }

        public static class DEFENCE_AREA_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int DEFENCE_AREA_TYPE_LEARN = 0;
            public static final int DEFENCE_AREA_TYPE_CLEAR = 1;
            public static final int DEFENCE_AREA_TYPE_CLEAR_GROUP = 2;

        }

        public static class REMOTE_DEFENCE_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int ALARM_SWITCH_ON = 1;
            public static final int ALARM_SWITCH_OFF = 0;
        }

        public static class REMOTE_RECORD_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int RECORD_SWITCH_ON = 1;
            public static final int RECORD_SWITCH_OFF = 0;
        }

        public static class PRE_RECORD_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int PRE_RECORD_SWITCH_ON = 1;
            public static final int PRE_RECORD_SWITCH_OFF = 0;
        }

        public static class INIT_PASSWORD_SET {
            public static final int SETTING_SUCCESS = 0;
            public static final int ALREADY_EXIST_PASSWORD = 43;
        }

        public static class DEVICE_UPDATE {

            public static final int HAVE_NEW_VERSION = 1;
            public static final int IS_LATEST_VERSION = 54;
            public static final int OTHER_WAS_CHECKING = 58;
            public static final int HAVE_NEW_IN_SD = 72;
            public static final int DO_UPDATE_SUCCESS = 0;
            public static final int UNKNOWN = -1;
        }

        public static class INFRARED_SWITCH {
            public static final int INFRARED_SWITCH_ON = 1;
            public static final int INFRARED_SWITCH_OFF = 0;
        }

        public static class WIRED_ALARM_INPUT {
            public static final int ALARM_INPUT_ON = 1;
            public static final int ALARM_INPUT_OFF = 0;
        }

        public static class WIRED_ALARM_OUT {
            public static final int ALARM_OUT_ON = 1;
            public static final int ALARM_OUT_OFF = 0;
        }

        public static class AUTOMATIC_UPGRADE {
            public static final int AUTOMATIC_UPGRADE_ON = 1;
            public static final int AUTOMATIC_UPGRADE_OFF = 0;
        }

        public static class DEVICE_VISITOR_PASSWORD_SET {
            public static final int SETTING_SUCCESS = 0;
        }

        public static class SD_FORMAT {
            public static final int SD_CARD_SUCCESS = 80;
            public static final int SD_CARD_FAIL = 81;
            public static final int SD_NO_EXIST = 82;
            public static final int BEING_VIDEO = 103;
        }
    }

    public static class Action {
        /*
         * globel
         */
        public final static String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
        public static final String CLOSE_INPUT_DIALOG = PACKAGE_NAME
                + "CLOSE_INPUT_DIALOG";
        public static final String NET_WORK_TYPE_CHANGE = PACKAGE_NAME
                + "NET_WORK_TYPE_CHANGE";

        /*
         * mainactivity
         */
        public final static String ACTION_SWITCH_USER = PACKAGE_NAME
                + "ACTION_SWITCH_USER";
        public final static String ACTION_EXIT = PACKAGE_NAME + "ACTION_EXIT";
        public static final String RECEIVE_MSG = PACKAGE_NAME + "RECEIVE_MSG";
        public final static String ACTION_UPDATE = PACKAGE_NAME
                + "ACTION_UPDATE";
        public static final String RECEIVE_SYS_MSG = PACKAGE_NAME
                + "RECEIVE_SYS_MSG";
        public static final String SESSION_ID_ERROR = PACKAGE_NAME
                + "SESSION_ID_ERROR";

        /*
         * contact
         */
        public final static String REFRESH_CONTANTS = PACKAGE_NAME
                + "refresh.contants";
        public final static String ACTION_REFRESH_NEARLY_TELL = PACKAGE_NAME
                + "ACTION_REFRESH_NEARLY_TELL";
        public final static String GET_FRIENDS_STATE = PACKAGE_NAME
                + "GET_FRIENDS_STATE";
        public final static String ACTION_COUNTRY_CHOOSE = PACKAGE_NAME
                + "ACTION_COUNTRY_CHOOSE";
        public final static String ADD_CONTACT_SUCCESS = PACKAGE_NAME
                + "ADD_CONTACT_SUCCESS";
        public final static String LOCAL_DEVICE_SEARCH_END = PACKAGE_NAME
                + "LOCAL_DEVICE_SEARCH_END";
        public final static String IS_OPEN_THREAD_CONTACTFRAG = PACKAGE_NAME
                + "IS_OPEN_THREAD_CONTACTFRAG";
        /*
         * MainControl
         */
        public static final String CHECK_DEVICE_PWD = PACKAGE_NAME
                + "CHECK_DEVICE_PWD";

        /*
         * sysset
         */
        public static final String ADD_ALARM_MASK_ID_SUCCESS = PACKAGE_NAME
                + "ADD_ALARM_MASK_ID_SUCCESS";
        public static final String REFRESH_ALARM_RECORD = PACKAGE_NAME
                + "REFRESH_ALARM_RECORD";
        public static final String CHANGE_ALARM_MESSAGE = PACKAGE_NAME
                + "CHANGE_ALARM_MESSAGE";

		/*
         * shake
		 */
//		public static final String UPDATE_DEVICE_FALG = PACKAGE_NAME
//				+ "UPDATE_DEVICE_FALG";

        /*
         * MainLogin
         */
        public static final String REPLACE_EMAIL_LOGIN = PACKAGE_NAME
                + "REPLACE_EMAIL_LOGIN";
        public static final String REPLACE_PHONE_LOGIN = PACKAGE_NAME
                + "REPLACE_PHONE_LOGIN";
        public static final String EMAIL_LOGIN = PACKAGE_NAME + "EMAIL_LOGIN";
        public static final String PHONE_LOGIN = PACKAGE_NAME + "PHONE_LOGIN";

        /*
         * Control
         */
        public static final String CONTROL_SETTING_PWD_ERROR = PACKAGE_NAME
                + "CONTROL_SETTING_PWD_ERROR";

        public static final String REPLACE_REMOTE_CONTROL = PACKAGE_NAME
                + "REPLACE_REMOTE_CONTROL";
        public static final String REPLACE_MAIN_CONTROL = PACKAGE_NAME
                + "REPLACE_MAIN_CONTROL";
        public static final String REPLACE_SETTING_TIME = PACKAGE_NAME
                + "REPLACE_SETTING_TIME";
        public static final String REPLACE_ALARM_CONTROL = PACKAGE_NAME
                + "REPLACE_ALARM_CONTROL";
        public static final String REPLACE_VIDEO_CONTROL = PACKAGE_NAME
                + "REPLACE_VIDEO_CONTROL";
        public static final String REPLACE_RECORD_CONTROL = PACKAGE_NAME
                + "REPLACE_RECORD_CONTROL";
        public static final String REPLACE_SECURITY_CONTROL = PACKAGE_NAME
                + "REPLACE_SECURITY_CONTROL";
        public static final String REPLACE_NET_CONTROL = PACKAGE_NAME
                + "REPLACE_NET_CONTROL";
        public static final String REPLACE_DEFENCE_AREA_CONTROL = PACKAGE_NAME
                + "REPLACE_DEFENCE_AREA_CONTROL";
        public static final String REPLACE_SD_CARD_CONTROL = PACKAGE_NAME
                + "REPLACE_SD_CARD_CONTROL";
        public static final String REPLACE_LANGUAGE_CONTROL = PACKAGE_NAME
                + "REPLACE_LANGUAGE_CONTROL";
        public static final String REPLACE_MODIFY_WIFI_PWD_CONTROL = PACKAGE_NAME
                + "REPLACE_MODIFY_WIFI_PWD_CONTROL";
        public static final String REPLACE_MODE_CONTROL = PACKAGE_NAME
                + "REPLACE_MODE_CONTROL";
        public static final String REPLACE_SMART_DEVICE_CONTROL = PACKAGE_NAME
                + "REPLACE_SMART_DEVICE_CONTROL";
        public static final String CONTROL_BACK = PACKAGE_NAME + "CONTROL_BACK";

        // 智能钥匙连接
        public static final String CURRENT_WIFI_NAME = PACKAGE_NAME
                + "CURRENT_WIFI_NAME";
        public static final String HEARED = PACKAGE_NAME + "HEARED";
        public static final String DELETE_DEVICE_ALL = PACKAGE_NAME
                + "DELETE_DEVICE_ALL";
        public static final String INSERT_INFRARED_BACK = PACKAGE_NAME
                + "INSERT_INFRARED_BACK";
        public static final String ADD_SUCESS = PACKAGE_NAME + "ADD_SUCESS";
        public static final String SETTING_WIFI_SUCCESS = PACKAGE_NAME
                + "SETTING_WIFI_SUCCESS";
        public static final String ACTIVITY_FINISH = PACKAGE_NAME
                + "ACTIVITY_FINISH";
        public static final String REPEAT_LOADING_DATA = PACKAGE_NAME
                + "REPEAT_LOADING_DATA";
        // add
        public static final String DIAPPEAR_ADD = PACKAGE_NAME + "DIAPPEAR_ADD";
        public static final String RADAR_SET_WIFI_SUCCESS = PACKAGE_NAME
                + "RADAR_SET_WIFI_SUCCESS";
        public static final String RADAR_SET_WIFI_FAILED = PACKAGE_NAME
                + "RADAR_SET_WIFI_FAILED";
        public static final String MONITOR_NEWDEVICEALARMING = PACKAGE_NAME
                + "MONITOR_NEWDEVICEALARMING";
        public static final String SEARCH_AP_DEVICE = PACKAGE_NAME
                + "SEARCH_AP_DEVICE";
        public static final String SET_AP_DEVICE_WIFI_PWD = PACKAGE_NAME
                + "SET_AP_DEVICE_WIFI_PWD";
        public static final String SEARCH_AP_APMODE = PACKAGE_NAME
                + "SEARCH_AP_APMODE";
        public static final String SEARCH_AP_ADDAPDEVICE = PACKAGE_NAME
                + "SEARCH_AP_ADDAPDEVICE";
        public static final String SEARCH_AP = PACKAGE_NAME + "SEARCH_AP";
        public static final String SEARCH_AP_QUITEAPDEVICE = PACKAGE_NAME
                + "SEARCH_AP_QUITEAPDEVICE";
        public static final String EXITE_AP_MODE = PACKAGE_NAME
                + "EXITE_AP_MODE";

        public static final String ENTER_DEVICE_SETTING = PACKAGE_NAME + "ENTER_DEVICE_SETTING";
        public static final String CALL_DEVICE = PACKAGE_NAME + "CALL_DEVICE";

        public static final String GET_DEVICE_TYPE = PACKAGE_NAME + "GET_DEVICE_TYPE";
        //		刷新报警信息
        public static final String REFRESH_ALARM_MESSAGE = PACKAGE_NAME + "REFRESH_ALARM_MESSAGE";
        //      刷新精品推荐消息
        public static final String REFRESH_SYSTEM_MESSAGE = PACKAGE_NAME + "refresh_system_message";
        //精品推荐条数
        public static final String SYSTEM_MESSAGE_COUNT = PACKAGE_NAME + "SYSTEM_MESSAGE_COUNT";

        public static final String CHANGE_REMOTE_DEFENCE = PACKAGE_NAME + "CHANGE_REMOTE_DEFENCE";
        //监控时，推送来，查看另一台
        public static final String NEW_MONITOR = PACKAGE_NAME + "NEW_MONITOR";
        //修改管理密码成功
        public static final String MODIFY_PASSWORD_SUCCESS = PACKAGE_NAME + "MODIFY_PASSWORD_SUCCESS";

        //FTP Control
        public static final String REPLACE_FTP_CONTROL = PACKAGE_NAME
                + "REPLACE_FTP_CONTROL";
        //监控门铃的同一台推送门铃
        public static final String MONITOR_ALARM_DOOR_PUSH = PACKAGE_NAME + "MONITOR_ALARM_DOOR_PUSH";
        //ehome
        public static final String EHOME_FRAG = PACKAGE_NAME + "EHOME_FRAG";
        //PIRLight
        public static final String PIRLIGHT_FRAG = PACKAGE_NAME + "PIRLIGHT_FRAG";
        //定时不撤防 control
        public static final String REPLACE_DEFENCE_CONTROL = PACKAGE_NAME
                + "REPLACE_DEFENCE_CONTROL";
        //设备信息
        public static final String REPLACE_DEVICE_INFO = PACKAGE_NAME + "REPLACE_DEVICE_INFO";

        //设备更新完成返回主界面
        public static final String REPLACE_BACK_MAINACTIVITY = PACKAGE_NAME + "REPLACE_BACK_MAINACTIVITY";
        //退出添加设备
        public static final String EXIT_ADD_DEVICE = PACKAGE_NAME + "EXIT_ADD_DEVICE";
        //有线添加成功
        public static final String WIRE_ADD_SUCCESS = PACKAGE_NAME + "WIRE_ADD_SUCCESS";
    }

    public static class DeviceState {
        public static final int ONLINE = 1;
        public static final int OFFLINE = 0;
    }

    public static class DefenceState {
        public static final int DEFENCE_STATE_OFF = 0;
        public static final int DEFENCE_STATE_ON = 1;
        public static final int DEFENCE_STATE_LOADING = 2;
        public static final int DEFENCE_STATE_WARNING_PWD = 3;
        public static final int DEFENCE_STATE_WARNING_NET = 4;
        public static final int DEFENCE_NO_PERMISSION = 5;
    }

    public static class APmodeState {
        public static final int LINK = 0;
        public static final int UNLINK = 1;
        public static final int NO_SEARCH = 2;
    }

    public static class DeviceFlag {
        public static final int UNSET_PASSWORD = 0;
        public static final int ALREADY_SET_PASSWORD = 1;
        public static final int AP_MODE = 2;
        public static final int UNKNOW = 3;
    }

    public static class ReplaceStyle {
        public static final int SLIDE = 0;
        public static final int FADE = 1;
    }

    public static class LoginType {
        public static final int PHONE = 0;
        public static final int EMAIL = 1;
    }

    public static class RegisterType {
        public static final int PHONE = 0;
        public static final int EMALL = 1;
    }

    public static class ModifyAccountType {
        public static final int MODIFY_EMAIL = 0;
    }

    public static class SettingConfig {

        public static final int SETTING_CLICK_TIME_DELAY = 0;

    }

    public static class MessageType {
        public static final int SEND_SUCCESS = 0;
        public static final int SENDING = 1;
        public static final int SEND_FAULT = 2;
        public static final int UNREAD = 3;
        public static final int READED = 4;
    }

    public static class ActivityInfo {
        public static final int ACTIVITY_LOGOACTIVITY = 0;
        public static final int ACTIVITY_MAINACTIVITY = 1;
        public static final int ACTIVITY_LOGINACTIVITY = 2;
        public static final int ACTIVITY_IMAGEBROWSER = 3;
        public static final int ACTIVITY_ABOUTACTIVITY = 4;
        public static final int ACTIVITY_ACCOUNTINFOACTIVITY = 5;
        public static final int ACTIVITY_ADDALARMMASKIDACTIVITY = 6;
        public static final int ACTIVITY_ADDCONTACTACTIVITY = 7;
        public static final int ACTIVITY_ADDCONTACTNEXTACTIVITY = 8;
        public static final int ACTIVITY_ALARMRECORDACTIVITY = 9;
        public static final int ACTIVITY_ALARMSETACTIVITY = 10;
        public static final int ACTIVITY_FORWARDACTIVITY = 11;
        public static final int ACTIVITY_MAINCONTROLACTIVITY = 12;
        public static final int ACTIVITY_MESSAGEACTIVITY = 13;
        public static final int ACTIVITY_MODIFYACCOUNTEMAILACTIVITY = 14;
        public static final int ACTIVITY_MODIFYACCOUNTPHONEACTIVITY = 15;
        public static final int ACTIVITY_MODIFYACCOUNTPHONEACTIVITY2 = 16;
        public static final int ACTIVITY_MODIFYALARMIDACTIVITY = 17;
        public static final int ACTIVITY_MODIFYBOUNDEMAILACTIVITY = 18;
        public static final int ACTIVITY_MODIFYCONTACTACTIVITY = 19;
        public static final int ACTIVITY_MODIFNPCPASSWORDACTIVITY = 20;
        public static final int ACTIVITY_REGISTERACTIVITY = 21;
        public static final int ACTIVITY_REGISTERACTIVITY2 = 22;
        public static final int ACTIVITY_SEARCHLISTACTIVITY = 23;
        public static final int ACTIVITY_SETTINGBELLRINGACTIVITY = 24;
        public static final int ACTIVITY_SETTINGSDBELLACTIVITY = 25;
        public static final int ACTIVITY_SETTINGSYSTEMACTIVITY = 26;
        public static final int ACTIVITY_SYSMSGACTIVITY = 27;
        public static final int ACTIVITY_SYSNOTIFYACTIVITY = 28;
        public static final int ACTIVITY_TELLDETAILACTIVITY = 29;
        public static final int ACTIVITY_UNBINDPHONEACTIVITY = 30;
        public static final int ACTIVITY_VERIFYPHONEACTIVITY = 31;
        public static final int ACTIVITY_PLAYBACKLISTACTIVITY = 32;
        public static final int ACTIVITY_PLAYBACKACTIVITY = 33;
        public static final int ACTIVITY_VIDEOACTIVITY = 34;
        public static final int ACTIVITY_MONITORACTIVITY = 35;
        public static final int ACTIVITY_CALLACTIVITY = 36;
        public static final int ACTIVITY_MODIFYLOGINPASSWORDACTIVITY = 37;
        public static final int ACTIVITY_CUTIMAGEACTIVITY = 38;
        public static final int ACTIVITY_FORWARDDOWNACTIVITY = 39;
        public static final int ACTIVITY_DEVICE_UPDATE = 40;
        public static final int ACTIVITY_SHAKE = 41;
        public static final int ACTIVITY_INFRARED_REMOTE = 42;
        public static final int ACTIVITY_INFRARED_SET_WIFI = 43;
        public static final int ACTIVITY_LOCAL_DEVICE_LIST = 44;
        public static final int ACTIVITY_ALTOGETHERREGISTERACTIVITY = 45;
        public static final int ACTIVITY_QRCODE = 46;
        public static final int ACTIVITY_MODIFY_NPC_VISITOR_PASSWORD_ACTIVITY = 47;
        public static final int ACTIVITY_THREEADDCONTACTACTIVITY = 48;
        public static final int SMARTKEYSETWIFIACTIVITY = 49;
        public static final int WIFILISTACTIVITY = 50;
        public static final int ACTIVITY_QRCODEACTIVITY = 51;
        public static final int ACTIVITY_CREATEQRCODEACTIVITY = 52;
        public static final int ACTIVITY_ALARMPUSHACCOUNTACTIVITY = 53;
        public static final int ACTIVITY_IMAGESEE = 54;
        public static final int ACTIVITY_RADARADDFIRSTACTIVITY = 55;
        public static final int ACTIVITY_RARDARADDACTIVITY = 56;
        public static final int ACTIVITY_WAITDEVICENETWORKACTIVITY = 57;
        public static final int ACTIVITY_ADDWAITACTIVITY = 58;
        public static final int ACTIVITY_ADDAPDEVICEACTIVITY = 59;
        public static final int ACTIVITY_APDEVICEMONITORACTIVITY = 60;
        public static final int ACTIVITY_WAITTING = 61;
        public static final int ACTIVITY_MODIFYAPWIFIPWD = 62;
        public static final int ACTIVITY_ALRAM_WITH_PICTRUE = 63;
        public static final int ACTIVITY_APMONITORACTIVITY = 64;
        public static final int ACTIVITY_ALRAM_PICTRUE = 65;
        public static final int ACTIVITY_SANQRCODEACTIVITY = 66;
        public static final int ACTIVITY_NVR_MONITOR = 67;
        public static final int ACTIVITY_NVR_PLAYBACK = 68;
        public static final int ACTIVITY_MALLACTIVITY = 69;
        public static final int ACTIVITY_RECOMMENDINFORMATIONACTIVITY = 70;
        public static final int ACTIVITY_RECOMMENDPRODUCTACTIVITY = 71;
        public static final int ACTIVITY_ADDGROUPTIME = 72;
        public static final int ACTIVITY_MODIFYSENSOR = 73;
        public static final int ACTIVITY_SENDSOUNDWAVEGUIDEACTIVITY = 74;
        public static final int ACTIVITY_DEFENCE_CONTROL = 75;
        public static final int ACTIVITY_DEFENCE_AREA_CONTROL = 76;
        public static final int ACTIVITY_BINDING_LOCATION = 77;
        public static final int ACTIVITY_FTP_CONTROL = 78;
        public static final int ACTIVITY_DEFENCE_SELECT_TIME = 79;
        public static final int ACTIVITY_RetPwd = 80;
        public static final int ACTIVITY_BINDING_LOCATION2 = 81;
        public static final int ACTIVITY_NVRPLAYBACKLISTACTIVITY = 82;
        public static final int ACTIVITY_REGISTER3 = 83;
        public static final int ACTIVITY_REGISTERBYPHONE = 84;
        public static final int ACTIVITY_REGISTERBYEMAIL = 85;
        public static final int ACTIVITY_RETPWDBYPHONE = 86;
        public static final int ACTIVITY_MAINREGISTER = 87;
        public static final int ACTIVITY_NETWORKMODEACTIVITY = 88;
        public static final int ACTIVITY_PREPARECAMERAACTIVITY = 89;
        public static final int ACTIVITY_DEVICEREADYACTIVITY = 90;
    }

    public static class ActivityStatus {
        public static final int STATUS_START = 0;
        public static final int STATUS_STOP = 1;
    }

    public static class Image {
        public static final String USER_HEADER_PATH = "/sdcard/"
                + CACHE_FOLDER_NAME + "/";
        public static final String USER_HEADER_TEMP_FILE_NAME = "temp";
        public static final float USER_HEADER_ROUND_SCALE = 1f / 32f;
        public static final int USER_HEADER_MIN_SIZE = 32;
        public static final String USER_HEADER_FILE_NAME = "header";
        public static final String USER_GRAY_HEADER_FILE_NAME = "header_gray";
    }

    public static class Update {
        public static final String SAVE_PATH = Constants.CACHE_FOLDER_NAME
                + "/apk";
        public static final String FILE_NAME = Constants.CACHE_FOLDER_NAME
                + ".apk";
        public static final String INSTALL_APK = "application/vnd.android.package-archive";
        public static final int CHECK_UPDATE_HANDLE_FALSE = 0x11;
        public static final int CHECK_UPDATE_HANDLE_TRUE = 0x12;
    }

    public static final int USER_HEADER_WIDTH_HEIGHT = 500;

    public static class Languege {
        public static final int LANG_EN = 0; // 0 默认 英文
        public static final int LANG_CHS = 1; // 1 中文简体
        public static final int LANG_JP = 2; // 2 日语
        public static final int LANG_PT = 3; // 3 葡萄牙
        public static final int LANG_SP = 4; // 4西班牙
        public static final int LANG_CF = 5; // 5 中文繁体
        public static final int LANG_FR = 6; // 6 法语
        public static final int LANG_RU = 7; // 7 俄语

    }

    public static class FOCUS {
        public static final byte FOCUS_BIG = 0x1;
        public static final byte FOCUS_SMALL = 0x11;
    }

    public static class CHANGE_FOCUS_ZOOM {
        public static final int CHANGE_NO = 0;
        public static final int CHANGE_ZOOM = 1;
        public static final int CHANGE_FOCUS = 2;
        public static final int CHANGE_FOCUS_ZOOM = 3;
    }

    public static class SensorType {
        public static final int TYPE_REMOTE_CONTROLLER = 0; // 0 遥控器
        public static final int TYPE_DOOR_STAUS_SWITCH = 1; // 1 门磁
        public static final int TYPE_SMOKE_TRANSDUCER = 2; // 2 烟雾传感器
        public static final int TYPE_GAS_SENSOR = 3; // 3 瓦斯探测传感器
        public static final int TYPE_LIGHT = 4; // 4 电灯
        public static final int TYPE_CURTAIN = 5; // 5 窗帘
        public static final int TYPE_JACK = 6;// 6 插座
        public static final int TYPE_PIR = 7; // 7 人体红外
        public static final int TYPE_WATER_INVADE = 8;//8 水浸
        public static final int TYPE_URGENCY = 9; //9  急救
        public static final int TYPE_WARNING_SPEAKER = 10; //10 警号
        public static final int TYPE_AIR_CONTROLER = 11; //空调

    }

    public static class FishBoption {
        public static final int MESG_SET_OK = 0;
        public static final int MESG_GET_OK = 1;
        public static final int MESG_SET_WORK_MODE_ERROR = 10;
        public static final int MESG_SET_SCHEDULE_WORK_MODE_ARG_ERROR = 11;
        public static final int MESG_DELETE_SCHEDULE_WORK_MODE_ARG_ERROR = 12;
        public static final int MESG_SET_SENSOR_WORK_MODE_ERROR = 13;
        public static final int MESG_SET_DELETE_ONE_CONTROLER_ERROR = 14;
        public static final int MESG_SET_DELETE_ONE_SENSOR_ERROR = 15;
        public static final int MESG_CHANGE_CONTROLER_NAME_ERROR = 16;
        public static final int MESG_CHANGE_SENSOR_NAME_ERROR = 17;
        public static final int MESG_LEARN_SENSOR_ERROR = 18;
        public static final int MESG_SET_SCHEDULE_HAVE_SAME_SCHEDULE = 19;
        public static final int MESG_SENSOR_NOT_LEARN_YET = 20;
        public static final int MESG_CANNOT_SHARE_TO_ITSELF = 21;
        public static final int MESG_DONOT_HAVE_PERMISSION_TO_SHARE = 22;
        public static final int MESG_DONOT_HAVE_PERMISSION_TO_DELETE = 23;
        public static final int MESG_ADMIN_CANNOT_DELETE_ITSELF_BEFORE_MEMBER_BEREMOVE = 24;
        public static final int MESG_MEMBERLIST_IS_EMPTY = 25;
        public static final int MESG_DONOT_HAVE_SUCH_MEMBER = 26;
        public static final int MESG_MEMBER_LIST_FULL = 27;
    }

    public static class LenStateCode {
        public static final int LEARN_STATE_IDLE = 0;
        public static final int LEARN_STATE_START = 1;
        public static final int LEARN_STATE_BUSY = 2;
        public static final int LEARN_STATE_TIMEOUT = 3;
        public static final int LEARN_STATE_SUCCESS = 4;
        public static final int LEARN_STATE_CONTROLER_FULL = 5;
        public static final int LEARN_STATE_SENSOR_FULL = 6;
        public static final int LEARN_STATE_ALREADY_LEARN = 7;
    }

    public static class SpecialSensorType {
        public static final int INDEX_ALARM_TYPE_MD = 0; // 0 移动侦测
        public static final int INDEX_ALARM_TYPE_ATTACH = 1; // 1 防攻击

    }

    public static class sensorSwitch {
        public final static int State_on = 1;
        public final static int State_off = 2;
        public final static int State_progress = 0;

    }

    public static class ConnectType {
        public final static int P2PCONNECT = 0;
        public final static int RTSPCONNECT = 1;
    }

    public static class SystemMessgeType {
        public static final int MALL_NEW = 1;
    }

    public static class FishMode {
        public static final int MODE_DEFULT = 0;
        public static final int MODE_HOME = 1;
        public static final int MODE_OUT = 2;
        public static final int MODE_SLEEP = 3;
    }

    public static class LightState {
        public static final int ON = 20;
        public static final int OFF = 21;
    }

    public static class AlarmPictrue {
        public static final int GET_FILE_CMD_ERR_NONE = 0;//获取文件成功
        public static final int GET_FILE_CMD_ERR_PW_INCRRECT = 1;//密码错误
        public static final int GET_FILE_CMD_ERR_IP_FREEZE = 2;//被冻结
        public static final int GET_FILE_CMD_ERR_NO_SUCH_FILE = 3;//没有需要的文件
        public static final int GET_FILE_CMD_ERR_NOT_ALLOWED = 4; //不允许
        public static final int GET_FILE_CMD_ERR_READ_FILE_FAIL = 5;//读文件出错
        public static final int GET_FILE_CMD_ERR_BUZY = 6;//忙线中
        public static final int GET_FILE_CMD_ERR_MEMORY_LIMITED = 7; //内存不足
        public static final int GET_FILE_CMD_ERR_TIMEOUT = 8; //连接超时
    }

    public static class SurportPrepoint {
        public static final int NO = 0;
        public static final int YES = 1;
    }

    //设备静音按钮状态
    public static class DeviceMuteState {
        public static final int NORMAL = 0;
        public static final int LOADING = 1;
        public static final int CLOSE = 2;
    }

    public static class DeviceType {
        public static int DT_ZY = 0;
        public static int DT_HS = 1;
    }

    public static class NetworkMode {
        public static final int WIFI = 0;
        public static final int WIRE = 1;
    }

    public static class AddDeviceMethod {
        public static final int LAN_ADD = 0;//局域网搜索添加
        public static final int WIFI_METHOD = 1;//设置wifi添加
        public static final int WIRE_METHOD = 2;//有线添加
    }

}
