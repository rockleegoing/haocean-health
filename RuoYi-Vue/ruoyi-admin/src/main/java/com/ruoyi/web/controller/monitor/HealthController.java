package com.ruoyi.web.controller.monitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查监控
 */
@RestController
public class HealthController {

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    /**
     * 兼容前端 /prod-api/health 路径
     */
    @GetMapping("/prod-api/health")
    public Map<String, Object> prodApiHealth() {
        return health();
    }
}
