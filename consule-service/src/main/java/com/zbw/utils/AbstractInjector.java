package com.zbw.utils;

import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

public abstract class AbstractInjector {

    protected RetryTemplate getRetryTemplate() {
        RetryTemplate template = new RetryTemplate();
        TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
        policy.setTimeout(100);
        template.setRetryPolicy(policy);
        return template;
    }

}
