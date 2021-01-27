package com.harry.alidns.client;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.harry.alidns.AliDnsApplication;
import com.harry.alidns.constant.Constants;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 阿里云客户端
 *
 * @author Created by Harry Ma on 2021-01-26
 */
@Component
public class AliClient {

    private static IAcsClient client;

    public AliClient() {
        IClientProfile profile = DefaultProfile.getProfile(Constants.REGION_ID, AliDnsApplication.aliConfig.getAccessKey(), AliDnsApplication.aliConfig.getAccessSecret());
        // 若报Can not find endpoint to access异常，请添加以下此行代码
        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
        client = new DefaultAcsClient(profile);
    }

    public String getFirstDomain() {
        DescribeDomainsRequest request = new DescribeDomainsRequest();
        DescribeDomainsResponse response;
        try {
            response = client.getAcsResponse(request);
            List<DescribeDomainsResponse.Domain> domains = response.getDomains();
            if (domains.size() > 0) {
                return domains.get(0).getDomainName();
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定解析记录
     *
     * @param domainName 域名
     * @param filterRR   获取的解析记录
     * @return
     */
    public DescribeDomainRecordsResponse.Record getRecord(String domainName, String filterRR) {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setDomainName(domainName);
        DescribeDomainRecordsResponse describeSubDomainRecordsResponse;
        try {
            describeSubDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
            for (DescribeDomainRecordsResponse.Record domainRecord : describeSubDomainRecordsResponse.getDomainRecords()) {
                if (filterRR != null && filterRR.equals(domainRecord.getRR())) {
                    return domainRecord;
                }
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRecordIp(String ip, String recordId, String rR) {
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRR(rR);
        request.setRecordId(recordId);
        request.setType(Constants.TYPE);
        request.setValue(ip);
        try {
            HttpResponse response = client.doAction(request);
            if (response.isSuccess()) {
                System.out.println("DNS解析成功^_^");
                return;
            }
            if (response.getHttpContentString().contains("The DNS record already exists.")){
                System.err.println("当前解析记录已存在...");
            }
            System.err.println("DNS解析失败,请检查$_$");
        } catch (ClientException e) {
            System.err.println(e.getErrMsg());
        }
    }
}
