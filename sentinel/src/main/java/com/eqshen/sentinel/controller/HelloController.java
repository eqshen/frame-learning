package com.eqshen.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSONObject;
import com.eqshen.sentinel.constant.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {

    @GetMapping("/say")
    public JSONObject sayHello(){
        JSONObject result = new JSONObject();
        try (final Entry entry = SphU.entry(SentinelResource.SAY_HELLO)){
            result.put("success",true);
            result.put("msg","欢迎!");
        }catch (Exception e){
            result.put("success",false);
            result.put("msg","请求过于频繁！");
            log.error("触发熔断！！！");
        }
        return result;
    }

    /**
     * 定义sentinel规则
     */
    @PostConstruct
    private void initFlowRules(){
        List<FlowRule> flowRules = new ArrayList<>();

        FlowRule flowRule = new FlowRule();
        flowRule.setResource(SentinelResource.SAY_HELLO);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);//限流规则类型
        flowRule.setCount(2); //qps阈值，每秒最多通过该值

        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
