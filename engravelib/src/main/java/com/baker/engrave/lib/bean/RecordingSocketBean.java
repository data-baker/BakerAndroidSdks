package com.baker.engrave.lib.bean;

/**
 * 流式上传请求类
 */
public class RecordingSocketBean {
    private HeaderBean header;
    private ParamBean param;
    private AudioBean audio;

    public RecordingSocketBean(HeaderBean header, ParamBean param, AudioBean audio) {
        this.header = header;
        this.param = param;
        this.audio = audio;
    }

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public AudioBean getAudio() {
        return audio;
    }

    public void setAudio(AudioBean audio) {
        this.audio = audio;
    }

    @Override
    public String toString() {
        return "RecordingSocketBean{" +
                "header=" + header.toString() +
                ", param=" + param.toString() +
                ", audio=" + audio.toString() +
                '}';
    }


    public static class ParamBean {

        /**
         * sessionId : 3447000ac199d95cd2aa1b62b94d9575fokpe1589165890917
         * originText : 我正在使用“留声机” 录制自己的声音。为宝宝留下声音的温暖，爱的陪伴。接下来就要正式开始录制了，这个故事叫做“小蝌蚪找妈妈”。
         */

        private String sessionId;
        private String originText;
        private String rerecordingFileName = "";//重录的文件名

        public ParamBean(String sessionId, String originText) {
            this.sessionId = sessionId;
            this.originText = originText;
        }

        public ParamBean() {
        }

        public ParamBean(String rerecordingFileName) {
            this.rerecordingFileName = rerecordingFileName;
        }

        public String getRerecordingFileName() {
            return rerecordingFileName;
        }

        public void setRerecordingFileName(String rerecordingFileName) {
            this.rerecordingFileName = rerecordingFileName;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getOriginText() {
            return originText;
        }

        public void setOriginText(String originText) {
            this.originText = originText;
        }

        @Override
        public String toString() {
            return "ParamBean{" +
                    "sessionId='" + sessionId + '\'' +
                    ", originText='" + originText + '\'' +
                    ", rerecordingFileName='" + rerecordingFileName + '\'' +
                    '}';
        }
    }

    public static class AudioBean {

        /**
         * socket首次连通服务器返回了后续需要用到的参数
         * dicPath : /home/zhongcaigui/vad2
         * id : websocketSessionId
         * fileName : 123456
         *
         * socket后续发送给服务器，客户端需要给服务器传递的参数
         * status : 0
         * sequence : 0
         * info : 123456
         * redisKey：keykeykey
         */

        /**
         * socket最终返回的识别结果
         * percent : 0.0
         * status : 1 //服务器返回的状态；与上面的客户端发送的state是同一个属性
         */

        private double percent;
        private int passStatus;

        private String dicPath;
        private String id;
        private String fileName;
        private int status;
        private int sequence;
        private String info;
        private String redisKey;
        private int type;

        public AudioBean() {
        }

        public AudioBean(int status, int sequence, String info) {
            this.status = status;
            this.sequence = sequence;
            this.info = info;
        }

        public AudioBean(String dicPath, String id, String fileName, int status, int sequence, String info) {
            this.dicPath = dicPath;
            this.id = id;
            this.fileName = fileName;
            this.status = status;
            this.sequence = sequence;
            this.info = info;
        }

        public int getPassStatus() {
            return passStatus;
        }

        public void setPassStatus(int passStatus) {
            this.passStatus = passStatus;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public double getPercent() {
            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }

        public String getRedisKey() {
            return redisKey;
        }

        public void setRedisKey(String redisKey) {
            this.redisKey = redisKey;
        }

        public String getDicPath() {
            return dicPath;
        }

        public void setDicPath(String dicPath) {
            this.dicPath = dicPath;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "AudioBean{" +
                    "percent=" + percent +
                    ", passStatus=" + passStatus +
                    ", dicPath='" + dicPath + '\'' +
                    ", id='" + id + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", status=" + status +
                    ", sequence=" + sequence +
                    ", info='" + info + '\'' +
                    ", redisKey='" + redisKey + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public static class HeaderBean {

        /**
         * token : token
         * userId : 202003311540546020000123754200006356
         * nounce : nounce
         * timestamp : timestamp
         * signature : 6db310aa3859f87b983ac55e60e87167
         */

        private String token;
        private String clientId;
        private String nounce;
        private String timestamp;
        private String signature;

        public HeaderBean(String token, String clientId, String nounce, String timestamp, String signature) {
            this.token = token;
            this.clientId = clientId;
            this.nounce = nounce;
            this.timestamp = timestamp;
            this.signature = signature;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getNounce() {
            return nounce;
        }

        public void setNounce(String nounce) {
            this.nounce = nounce;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public String toString() {
            return "HeaderBean{" +
                    "token='" + token + '\'' +
                    ", userId='" + clientId + '\'' +
                    ", nounce='" + nounce + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", signature='" + signature + '\'' +
                    '}';
        }
    }


}
