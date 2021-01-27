package com.harry.alidns;

import com.alibaba.fastjson.JSONObject;
import com.harry.alidns.constant.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class AliDnsApplication {

    public static final AliConfig aliConfig = new AliConfig();

    public static void main(String[] args) throws InterruptedException {
        Map<String, String> params = convertArgs(args);
        aliConfig.setAccessKey(params.get(AliConfig.ACCESS_KEY));
        aliConfig.setAccessSecret(params.get(AliConfig.ACCESS_KEY_SECRET));
        aliConfig.setRr(params.get(AliConfig.RR));
        aliConfig.validate();
        SpringApplication.run(AliDnsApplication.class, args);
        System.out.println("服务启动成功^_^");
        System.out.printf("当前使用的参数为:%s", JSONObject.toJSONString(aliConfig));
        new CountDownLatch(1).await();
    }


    private static Map<String, String> convertArgs(String[] args) {
        Map<String, String> result = new HashMap<>();
        for (String arg : args) {
            if (!arg.contains(Constants.SEPARATOR_STR)){
                continue;
            }
            String key = arg.substring(0, arg.indexOf(Constants.SEPARATOR_STR));
            String value = arg.substring(arg.indexOf(Constants.SEPARATOR_STR) + 1);
            result.put(key, value);
        }
        return result;
    }

    public static class AliConfig{

        public static final String ACCESS_KEY = "accessKey";

        public static final String ACCESS_KEY_SECRET = "accessSecret";

        public static final String RR = "rr";

        private String accessKey;

        private String accessSecret;

        private String rr;

        public AliConfig() {
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getAccessSecret() {
            return accessSecret;
        }

        public void setAccessSecret(String accessSecret) {
            this.accessSecret = accessSecret;
        }

        public String getRr() {
            return rr;
        }

        public void setRr(String rr) {
            this.rr = rr;
        }

        public void validate() {
            StringBuilder msg = new StringBuilder();
            if (accessKey == null || accessKey.equals(Constants.BLANK_STR)) {
                msg.append(ACCESS_KEY).append(Constants.COMMA);
            }
            if (accessSecret == null || accessSecret.equals(Constants.BLANK_STR)){
                msg.append(ACCESS_KEY_SECRET).append(Constants.COMMA);
            }
            if (rr == null || rr.equals(Constants.BLANK_STR)){
                msg.append(RR).append(Constants.COMMA);
            }
            if (msg.length() == 0){
                return;
            }
            throw new RuntimeException(String.format("[%s]参数不能为空", msg.delete(msg.length() - 1 , msg.length()).toString()));

        }
    }

}
