package com.appleframework.server.test.model;

public class Status {
    
    private byte[] original;
    
    private String sn;
    
    public Status(byte[] original, String sn) {
        this.original = original;
        this.sn = sn;
    }
    
    public int[] valueOf() {
        int[] result = new int[]{ 0x00, 0x00, 0x00, 0x00 };
        result[3] |= this.getAcc_41();
        result[3] |= this.getFixedPosition_42();
        result[3] |= this.getEastLngOrWestLng_43();
        result[3] |= this.getNorthLatOrSouthLat_44();
        result[3] |= this.getSupplements_47();

        result[2] |= this.getOil_33();//判断油电
        result[2] |= this.getCircuit_34();//判断断电
        //if(this.isGpsData()) {
            // result[2] |= this.getHavingLbs_36();
            result[2] |= this.getGps_37();
        //}
        
        result[1] |= this.getSleep_23();
        
        // 无经纬度数据，导致平台无法定位，故将定位数据位置0（未定位）
        if(this.getSleep_23() == 2) {
            result[3] &= 0xfd;
        }
        return result;
    }
    
    private int getSleep_23() {
        if(this.isHeartbeatData() || this.isLoginData()) {
            return 1 << 1;
        }
        return 0;
    }
    
    private int getGps_37() {
        if(this.isGpsData()) {
            if(this.getFixedPosition_42() == 0) {
                return 1 << 6;
            }
        }
        return 0;
    }
    
    @SuppressWarnings("unused")
    private int getHavingLbs_36() {
        if(this.getFixedPosition_42() == 0) {
            return 1 << 5;
        }
        return 0;
    }
    
    private int getCircuit_34() {
        if(this.isAlarmData()) {
            if((this.original[31] & 56) == 16) {
                return 1 << 3;
            }else if((this.original[31] & 56) == 0) {
                //000表示恢复
                return 0;
            }else{
                //如果即不为010又不为000，判断是否接电源充电
                return (this.original[31] & 4) == 4 ? 0 : (1 << 3);//接电时恢复断电,等于4是接电;
            }
        } else {
            if(AlarmStatus.powerFailureAlarmMap.containsKey(this.sn)) {
                return AlarmStatus.powerFailureAlarmMap.get(this.sn) << 3;
            }
        }
        return 0;
    }
    
    private int getOil_33() {
        if(this.isAlarmData()) {
            if(((this.original[31] >> 7) & 0x01) == 1) {
                return 1 << 2;
            }
        }
        if(this.isHeartbeatData()) {
            if(((this.original[4] >> 7) & 0x01) == 1) {
                return 1 << 2;
            }
        }
        return 0;
    }
    
    private int getSupplements_47() {
        if(this.isGpsData()) {
            if(this.original[32] == 1) {
                return 1 << 7;
            }
        }
        return 0;
    }
    
    private int getNorthLatOrSouthLat_44() {
        if(this.isGpsData() || this.isAlarmData()) {
            if(this.original[20] >> 2 == 0) {
                return 1 << 2;
            }
        } 
        return 0;
    }

    private int getEastLngOrWestLng_43() {
        if(this.isGpsData() || this.isAlarmData()) {
            if(((this.original[20] >> 3) & 0x01) == 1) {
                return 1 << 3;
            }
        } 
        return 0;
    }
    
    private int getFixedPosition_42() {
        if(this.isGpsData()) {
            if(((this.original[20] >> 4) & 0x01) == 1) {
                return 1 << 1;
            }
        } 
        if(this.isAlarmData()) {
            //判断gps是否定位
            //2017-10-10 11:00wb注释掉，直接返回0，不定位；原因：015老版设备上传的0x26数据会出现gps定位，但是经纬度为0的情况，曾希文提出将0x26数据直接写成gps不定位；由黄总确认就这么改
            /*if(((this.original[31] >> 6) & 0x01) == 1) {
                return 1 << 1;
            }*/
            return 0;
        }
        if(this.isHeartbeatData()) {
            if(((this.original[4] >> 6) & 0x01) == 1) {
                return 1 << 1;
            }
        }
        return 0;
    }

    private int getAcc_41() {
        if(this.isAlarmData()) {
            if(((this.original[31] >> 1) & 0x01) == 1) {
                return 1;
            }
        }
        if(this.isHeartbeatData()) {
            if(((this.original[4] >> 1) & 0x01) == 1) {
                return 1;
            }
        }
        if(this.isGpsData()) {
            if(this.original[30] == 1) {
                return 1;
            }
        }
        return 0;
    }
    
    public boolean isAlarmData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x26;
        } 
        return this.original[4] == 0x26;
    }
    
    public boolean isGpsData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x22;
        } 
        return this.original[4] == 0x22;
    }
    
    public boolean isHeartbeatData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x13;
        } 
        return this.original[4] == 0x13;
    }
    
    public boolean isLoginData() {
        if(this.isStartWith0x78()) {
            return this.original[3] == 0x01;
        } 
        return this.original[4] == 0x01;
    }
    
    public boolean isStartWith0x78() {
        return this.original[0] == 0x78 && this.original[1] == 0x78;
    }
    
}
