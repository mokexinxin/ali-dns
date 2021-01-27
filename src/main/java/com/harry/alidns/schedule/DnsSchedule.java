package com.harry.alidns.schedule;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.harry.alidns.AliDnsApplication;
import com.harry.alidns.client.AliClient;
import com.harry.alidns.constant.Constants;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * 定时修改阿里云动态DNS
 *
 * @author Created by Harry Ma on 2021-01-26
 */
@Component
@EnableScheduling
public class DnsSchedule {


    @Resource
    private AliClient aliClient;

    /**
     * 每10分钟修改一次云解析记录
     */
    @Scheduled(fixedRate = 1000 * 60 * 10)
    private void dnsTasks() {
        String nowIp = getNowIp();
        if (nowIp == null || nowIp.equals("")) {
            System.err.println("获取当前IP为空，请检查");
            return;
        }
        String firstDomain = aliClient.getFirstDomain();
        System.out.printf("获取到的domain为: %s%n", firstDomain);
        DescribeDomainRecordsResponse.Record record = aliClient.getRecord(firstDomain, AliDnsApplication.aliConfig.getRr());
        System.out.printf("获取到指定RR(%s)为: %s%n", AliDnsApplication.aliConfig.getRr(), JSONObject.toJSONString(record));
        if (!nowIp.equals(record.getValue())){
            aliClient.updateRecordIp(nowIp, record.getRecordId(), AliDnsApplication.aliConfig.getRr());
        }
        System.out.println("当前IP与解析IP相同，无需更新...");
    }

    private String getNowIp() {
        HttpGet httpGet = new HttpGet(Constants.REQUEST_IP_URL);
        httpGet.addHeader(Constants.ACCEPT_STR, Constants.ACCEPT_VALUE);
        httpGet.addHeader(Constants.CONNECTION_STR, Constants.CONNECTION_VALUE);
        httpGet.addHeader(Constants.USER_AGENT_STR, Constants.USER_AGENT_VALUE);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                return Objects.requireNonNull(JSONObject.parseObject(result)).getString(Constants.IP_STR);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return "";
    }
}
