package com.pharos.walker.beans;

import java.util.List;

public class DoctorTeamBean {

    /**
     * code : 0
     * data : [{"checkDesc":"法罗适测试","createTime":"2023-02-27 11:44:36","deptId":1,"doctorTeamPeopleList":[{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eq9xjtnGBp57hYv5ibvZtQSOSK1eCiaLPyIsutDM2Dn0r3hCeArqYCIUm4WTqO9AI5tjQXo6kPBghoQ/132","id":"72","teamId":24,"userId":2357,"userName":"Cycle"},{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/sMVgcQxTv34ib0nfwLe0XDB5RlsianWsWmHJmGERic4P6HM7fiacVQMRJYia7vhbh7kEJ6INolYribLNGZa6ic98l0Uug/132","id":"73","teamId":24,"userId":2355,"userName":"rj117384"}],"hospitalId":58,"id":24,"leaderId":2306,"model":2,"name":"强盛集团","qrCode":"","status":1,"teamDesc":"法罗适软件测试"},{"checkDesc":"","createTime":"2023-03-27 14:23:31","deptId":1,"doctorTeamPeopleList":[{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eq9xjtnGBp57hYv5ibvZtQSOSK1eCiaLPyIsutDM2Dn0r3hCeArqYCIUm4WTqO9AI5tjQXo6kPBghoQ/132","id":"1082","teamId":137,"userId":2357,"userName":"Cycle"},{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/dQymAf17hVeGzOpIaapXftyv7h76sDFNZbd3zgj7dNOuAEUbH2VJNCXKjkjX99agVm5UyiaVNBsZzf1ET3aD0FQ/132","id":"1083","teamId":137,"userId":2356,"userName":"字母伎"},{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/sMVgcQxTv34ib0nfwLe0XDB5RlsianWsWmHJmGERic4P6HM7fiacVQMRJYia7vhbh7kEJ6INolYribLNGZa6ic98l0Uug/132","id":"1084","teamId":137,"userId":2355,"userName":"rj117384"},{"avatar":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/poster/1678005970285_YES.png","id":"1085","teamId":137,"userId":2329,"userName":"陈碧尧"}],"hospitalId":59,"id":137,"leaderId":2329,"model":2,"name":"法罗适团队123","qrCode":"https://ewj-pharos.oss-cn-hangzhou.aliyuncs.com/poster/1679898216894_YES.png","status":1,"teamDesc":"测试测试"}]
     */

    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * checkDesc : 法罗适测试
         * createTime : 2023-02-27 11:44:36
         * deptId : 1
         * doctorTeamPeopleList : [{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eq9xjtnGBp57hYv5ibvZtQSOSK1eCiaLPyIsutDM2Dn0r3hCeArqYCIUm4WTqO9AI5tjQXo6kPBghoQ/132","id":"72","teamId":24,"userId":2357,"userName":"Cycle"},{"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/sMVgcQxTv34ib0nfwLe0XDB5RlsianWsWmHJmGERic4P6HM7fiacVQMRJYia7vhbh7kEJ6INolYribLNGZa6ic98l0Uug/132","id":"73","teamId":24,"userId":2355,"userName":"rj117384"}]
         * hospitalId : 58
         * id : 24
         * leaderId : 2306
         * model : 2
         * name : 强盛集团
         * qrCode :
         * status : 1
         * teamDesc : 法罗适软件测试
         */

        private String checkDesc;
        private String createTime;
        private int deptId;
        private int hospitalId;
        private int id;
        private int leaderId;
        private int model;
        private String name;
        private String qrCode;
        private int status;
        private String teamDesc;
        private List<DoctorTeamPeopleListBean> doctorTeamPeopleList;

        public String getCheckDesc() {
            return checkDesc;
        }

        public void setCheckDesc(String checkDesc) {
            this.checkDesc = checkDesc;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getDeptId() {
            return deptId;
        }

        public void setDeptId(int deptId) {
            this.deptId = deptId;
        }

        public int getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(int hospitalId) {
            this.hospitalId = hospitalId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLeaderId() {
            return leaderId;
        }

        public void setLeaderId(int leaderId) {
            this.leaderId = leaderId;
        }

        public int getModel() {
            return model;
        }

        public void setModel(int model) {
            this.model = model;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTeamDesc() {
            return teamDesc;
        }

        public void setTeamDesc(String teamDesc) {
            this.teamDesc = teamDesc;
        }

        public List<DoctorTeamPeopleListBean> getDoctorTeamPeopleList() {
            return doctorTeamPeopleList;
        }

        public void setDoctorTeamPeopleList(List<DoctorTeamPeopleListBean> doctorTeamPeopleList) {
            this.doctorTeamPeopleList = doctorTeamPeopleList;
        }

        public static class DoctorTeamPeopleListBean {
            /**
             * avatar : https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eq9xjtnGBp57hYv5ibvZtQSOSK1eCiaLPyIsutDM2Dn0r3hCeArqYCIUm4WTqO9AI5tjQXo6kPBghoQ/132
             * id : 72
             * teamId : 24
             * userId : 2357
             * userName : Cycle
             */

            private String avatar;
            private String id;
            private int teamId;
            private int userId;
            private String userName;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getTeamId() {
                return teamId;
            }

            public void setTeamId(int teamId) {
                this.teamId = teamId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }
}
